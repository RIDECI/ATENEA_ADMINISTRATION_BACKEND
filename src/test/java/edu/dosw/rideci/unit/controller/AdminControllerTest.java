package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.ReportUseCase;
import edu.dosw.rideci.domain.model.ExportedReport;
import edu.dosw.rideci.domain.model.SecurityReport;
import edu.dosw.rideci.infrastructure.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController controller;

    @Mock
    private ReportUseCase reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReport() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();
        when(reportService.createReport(r)).thenReturn(r);

        ResponseEntity<SecurityReport> res = controller.createReport(r);

        assertEquals(200, res.getStatusCodeValue());
        assertEquals("1", res.getBody().getId());
    }

    @Test
    void shouldListReports() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();
        when(reportService.listReports(null, null, null)).thenReturn(List.of(r));
        ResponseEntity<List<SecurityReport>> res = controller.listReports(null, null, null);

        assertEquals(200, res.getStatusCodeValue());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldExportReports() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();
        when(reportService.listReports(null, null, null)).thenReturn(List.of(r));

        byte[] content = "pdfbytes".getBytes();
        ExportedReport exported = new ExportedReport(content, "reports.pdf", "application/pdf");
        when(reportService.exportReportsAs("pdf", List.of(r))).thenReturn(exported);
        ResponseEntity<byte[]> res = controller.exportReports(null, "pdf");

        assertEquals(200, res.getStatusCodeValue());
        assertEquals("attachment; filename=\"reports.pdf\"", res.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.parseMediaType("application/pdf"), res.getHeaders().getContentType());
        assertArrayEquals(content, res.getBody());
    }
}
