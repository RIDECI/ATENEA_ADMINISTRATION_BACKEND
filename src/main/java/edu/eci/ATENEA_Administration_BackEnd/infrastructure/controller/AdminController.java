package edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Crear un nuevo reporte de seguridad")
    public ResponseEntity<SecurityReport> createReport(@RequestBody SecurityReport r) {
        return ResponseEntity.ok(reportService.createReport(r));
    }

    @GetMapping("/reports")
    @Operation(summary = "Listar reportes de seguridad con filtros opcionales")
    public ResponseEntity<List<SecurityReport>> listReports(
            @RequestParam(required=false) String type,
            @RequestParam(required=false) String from,
            @RequestParam(required=false) String to) {
        LocalDateTime f = from == null ? null : LocalDateTime.parse(from);
        LocalDateTime t = to == null ? null : LocalDateTime.parse(to);
        return ResponseEntity.ok(reportService.listReports(type, f, t));
    }


    @GetMapping("/reports/export")
    @Operation(summary = "Exportar reportes a formato PDF, CSV o XLSX")
    public ResponseEntity<byte[]> exportReports(
            @RequestParam(required=false) String type,
            @RequestParam(defaultValue = "xlsx") String format) {

        List<SecurityReport> data = reportService.listReports(type, null, null);

        ReportService.ExportedReport exported = reportService.exportReportsAs(format, data);

        MediaType contentType = MediaType.parseMediaType(exported.mediaType());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exported.filename() + "\"")
                .contentType(contentType)
                .body(exported.content());
    }
}
