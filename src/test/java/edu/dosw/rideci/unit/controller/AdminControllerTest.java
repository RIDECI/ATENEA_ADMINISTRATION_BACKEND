package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.ReportUseCase;
import edu.dosw.rideci.domain.model.ExportedReport;
import edu.dosw.rideci.domain.model.SecurityReport;
import edu.dosw.rideci.infrastructure.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("1", res.getBody().getId());
    }

    @Test
    void shouldListReports() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();
        when(reportService.listReports(null, null, null)).thenReturn(List.of(r));
        ResponseEntity<List<SecurityReport>> res = controller.listReports(null, null, null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
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

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("attachment; filename=\"reports.pdf\"", res.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.parseMediaType("application/pdf"), res.getHeaders().getContentType());
        assertArrayEquals(content, res.getBody());
    }

    @Test
    void shouldListReportsWhitDate() {
        SecurityReport r = SecurityReport.builder().id("1").title("t").build();
        when(reportService.listReports(any(), any(), any())).thenReturn(List.of(r));
        String from = "2025-01-02T10:00:00";
        String to   = "2025-01-03T11:30:00";

        ResponseEntity<List<SecurityReport>> res = controller.listReports("INC", from, to);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());

        ArgumentCaptor<LocalDateTime> capFrom = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> capTo   = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(reportService, times(1)).listReports(eq("INC"), capFrom.capture(), capTo.capture());

        assertEquals(LocalDateTime.parse(from), capFrom.getValue());
        assertEquals(LocalDateTime.parse(to), capTo.getValue());
    }

}
