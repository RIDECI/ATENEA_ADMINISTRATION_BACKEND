package edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.eci.ATENEA_Administration_BackEnd.application.service.ReportService;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.SecurityReport;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;


    @PostMapping("/reports")
    public ResponseEntity<SecurityReport> createReport(@RequestBody SecurityReport r) {
        return ResponseEntity.ok(reportService.createReport(r));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<SecurityReport>> listReports(
            @RequestParam(required=false) String type,
            @RequestParam(required=false) String from,
            @RequestParam(required=false) String to) {
        LocalDateTime f = from == null ? null : LocalDateTime.parse(from);
        LocalDateTime t = to == null ? null : LocalDateTime.parse(to);
        return ResponseEntity.ok(reportService.listReports(type, f, t));
    }


    @GetMapping("/reports/export")
    public ResponseEntity<byte[]> exportReports(
            @RequestParam(required=false) String type) {

        List<SecurityReport> data = reportService.listReports(type, null, null);
        byte[] bytes = reportService.exportReportsToExcelBytes(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
