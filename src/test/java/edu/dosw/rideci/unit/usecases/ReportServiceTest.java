package edu.dosw.rideci.unit.usecases;

import com.lowagie.text.DocumentException;
import edu.dosw.rideci.application.service.ReportService;
import edu.dosw.rideci.application.port.out.SecurityReportRepositoryPort;
import edu.dosw.rideci.domain.model.ExportedReport;
import edu.dosw.rideci.domain.model.SecurityReport;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @InjectMocks
    private ReportService service;

    @Mock
    private SecurityReportRepositoryPort reportRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReportSetCreatedAtAndDefaultStatus() {
        SecurityReport r = SecurityReport.builder()
                .id("r1")
                .title("t")
                .type("INCIDENT")
                .createdBy(100L)
                .description("desc")
                .build();

        when(reportRepo.save(any(SecurityReport.class))).thenAnswer(inv -> inv.getArgument(0));

        SecurityReport saved = service.createReport(r);

        assertNotNull(saved.getCreatedAt());
        assertEquals("OPEN", saved.getStatus());
        verify(reportRepo, times(1)).save(any(SecurityReport.class));
    }

    @Test
    void shouldListReportsByTypeFromPeriod() {
        SecurityReport r1 = SecurityReport.builder().id("a").type("X").build();
        when(reportRepo.findByType("X")).thenReturn(List.of(r1));
        var byType = service.listReports("X", null, null);
        assertEquals(1, byType.size());
        verify(reportRepo, times(1)).findByType("X");

        var from = LocalDateTime.now().minusDays(2);
        var to = LocalDateTime.now();
        SecurityReport r2 = SecurityReport.builder().id("b").type("Y").build();
        when(reportRepo.findByPeriod(from, to)).thenReturn(List.of(r2));
        var byPeriod = service.listReports(null, from, to);
        assertEquals(1, byPeriod.size());
        verify(reportRepo, times(1)).findByPeriod(from, to);

        SecurityReport r3 = SecurityReport.builder().id("c").type("Z").build();
        when(reportRepo.findAll()).thenReturn(List.of(r3));
        var all = service.listReports(null, null, null);
        assertEquals(1, all.size());
        verify(reportRepo, times(1)).findAll();
    }

    @Test
    void shouldExportReportsToCsvEscapeAndHeaders() {
        SecurityReport r = SecurityReport.builder()
                .id("1")
                .title("My \"Title\", extra")
                .type("INC")
                .createdBy(10L)
                .occurredAt(LocalDateTime.of(2025,1,1,8,0))
                .createdAt(LocalDateTime.of(2025,1,1,9,0))
                .status("OPEN")
                .description("desc, with comma")
                .build();

        String csv = service.exportReportsToCsv(List.of(r));

        assertTrue(csv.startsWith("id,title,type,createdBy,occurredAt,createdAt,status,description,relatedId,severity"));
        assertTrue(csv.contains("\"My \"\"Title\"\", extra\""));
        assertTrue(csv.contains("\"desc, with comma\""));
    }

    @Test
    void shouldExportReportsToXlsxAndReturnBytes() {
        SecurityReport r = SecurityReport.builder().id("x1").title("t1").build();

        byte[] bytes = service.exportReportsToXlsx(List.of(r));

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void shouldExportReportsToPdfAndReturnBytes() {
        SecurityReport r = SecurityReport.builder().id("p1").title("t1").build();

        byte[] bytes = service.exportReportsToPdf(List.of(r));

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void shouldExportReportsAsFormats() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();

        ExportedReport pdf = service.exportReportsAs("pdf", List.of(r));
        assertEquals("reports.pdf", pdf.filename());
        assertEquals("application/pdf", pdf.mediaType());
        assertTrue(pdf.content().length > 0);

        ExportedReport csv = service.exportReportsAs("csv", List.of(r));
        assertEquals("reports.csv", csv.filename());
        assertTrue(new String(csv.content()).startsWith("id,title"));

        ExportedReport xlsx = service.exportReportsAs(null, List.of(r));
        assertEquals("reports.xlsx", xlsx.filename());
        assertTrue(xlsx.content().length > 0);
    }

    @Test
    void shouldExportXlsxWhenWorkbookWriteFailsThrowsReportExportException() {
        ReportService svc = new ReportService(reportRepo) {
            @Override
            protected Workbook createWorkbook() {
                Workbook mockW = mock(Workbook.class);
                try {
                    doThrow(new IOException("simulated IO error")).when(mockW).write(any(ByteArrayOutputStream.class));
                } catch (IOException ex) {
                    // ignored, Sonar-compliant: configuring mock behavior
                }
                return mockW;
            }
        };

        SecurityReport r = SecurityReport.builder().id("x1").title("t1").build();
        assertThrows(edu.dosw.rideci.application.exceptions.ReportExportException.class,
                () -> svc.exportReportsToXlsx(List.of(r)));
    }

    @Test
    void shouldExportPdfWhenPdfWriterCreationFailsThrowsReportExportException() {
        ReportService svc = new ReportService(reportRepo) {
            @Override
            protected com.lowagie.text.pdf.PdfWriter createPdfWriter(com.lowagie.text.Document document, ByteArrayOutputStream out) throws DocumentException {
                throw new DocumentException("simulated pdf error");
            }
        };

        SecurityReport r = SecurityReport.builder().id("p1").title("t1").build();
        assertThrows(edu.dosw.rideci.application.exceptions.ReportExportException.class,
                () -> svc.exportReportsToPdf(List.of(r)));
    }

    @Test
    void shouldExportReportsToExcelBytesDelegatesToXlsx() {
        ReportService spy = Mockito.spy(service);
        SecurityReport r = SecurityReport.builder().id("e1").title("t-e").build();
        byte[] fake = "abc".getBytes();
        Mockito.doReturn(fake).when(spy).exportReportsToXlsx(anyList());
        byte[] result = spy.exportReportsToExcelBytes(List.of(r));
        assertArrayEquals(fake, result);
        Mockito.verify(spy).exportReportsToXlsx(anyList());
    }
}
