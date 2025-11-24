package edu.dosw.rideci.application.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.dosw.rideci.exceptions.ReportExportException;
import edu.dosw.rideci.application.port.out.SecurityReportRepositoryPort;
import edu.dosw.rideci.domain.model.SecurityReport;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Servicio para gestión de reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SecurityReportRepositoryPort reportRepo;
    private static final List<String> HEADERS = List.of(
            "id", "title", "type", "createdBy", "occurredAt", "createdAt", "status", "description"
    );

    /**
     * Contenedor inmutable para resultado de exportación.
     * hace copia defensiva del arreglo de bytes en constructor.
     */
    public static final class ExportedReport {
        private final byte[] content;
        private final String filename;
        private final String mediaType;

        /**
         * Record que representa un reporte exportado con su contenido y metadatos
         *
         * @param content Bytes del contenido del archivo
         * @param filename Nombre del archivo generado
         * @param mediaType Tipo MIME del archivo
         */
        public ExportedReport(byte[] content, String filename, String mediaType) {
            this.content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
            this.filename = filename;
            this.mediaType = mediaType;
        }

        /**
         * Retorna una copia segura del contenido del archivo
         *
         * @return Copia del array de bytes del contenido
         */
        public byte[] content() {
            return Arrays.copyOf(content, content.length);
        }

        /**
         * Retorna el nombre del archivo generado
         *
         * @return Nombre del archivo
         */
        public String filename() {
            return filename;
        }

        /**
         * Retorna el tipo MIME del archivo
         *
         * @return Tipo MIME como "application/pdf", "text/csv"
         */
        public String mediaType() {
            return mediaType;
        }

        /**
         * Compara esta instancia con otro objeto para igualdad
         *
         * @param o Objeto a comparar
         * @return true si los objetos son iguales, false en caso contrario
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExportedReport)) return false;
            ExportedReport that = (ExportedReport) o;
            return Arrays.equals(content, that.content)
                    && Objects.equals(filename, that.filename)
                    && Objects.equals(mediaType, that.mediaType);
        }

        /**
         * Retorna el código hash de esta instancia
         *
         * @return Código hash calculado basado en contenido, filename y mediaType
         */
        @Override
        public int hashCode() {
            int result = Objects.hash(filename, mediaType);
            result = 31 * result + Arrays.hashCode(content);
            return result;
        }

        /**
         * Retorna representación en string de esta instancia
         *
         * @return String con información del reporte exportado (excluye el contenido completo por seguridad)
         */
        @Override
        public String toString() {
            return "ExportedReport{" +
                    "contentLength=" + (content == null ? 0 : content.length) +
                    ", filename='" + filename + '\'' +
                    ", mediaType='" + mediaType + '\'' +
                    '}';
        }
    }


    /**
     * Crea un nuevo reporte de seguridad
     *
     * @param r Reporte de seguridad a crear
     * @return Reporte de seguridad creado
     */
    public SecurityReport createReport(SecurityReport r) {
        r.setCreatedAt(LocalDateTime.now());
        r.setStatus(r.getStatus() == null ? "OPEN" : r.getStatus());
        return reportRepo.save(r);
    }

    /**
     * Lista reportes de seguridad con filtros opcionales
     *
     * @param type Tipo de reporte (opcional)
     * @param from Fecha de inicio (opcional)
     * @param to   Fecha de fin (opcional)
     * @return Lista de reportes de seguridad
     */
    public List<SecurityReport> listReports(String type, LocalDateTime from, LocalDateTime to) {
        if (type != null && !type.isBlank()) return reportRepo.findByType(type);
        if (from != null && to != null) return reportRepo.findByPeriod(from, to);
        return reportRepo.findAll();
    }


    /**
     * Exporta reportes al formato especificado
     *
     * @param format Formato de exportación (pdf, csv, xlsx)
     * @param reports Lista de reportes a exportar
     * @return ExportedReport con contenido y metadatos del archivo
     */
    public ExportedReport exportReportsAs(String format, List<SecurityReport> reports) {
        String fmt = format == null ? "xlsx" : format.trim().toLowerCase();
        switch (fmt) {
            case "pdf":
                byte[] pdf = exportReportsToPdf(reports);
                return new ExportedReport(pdf, "reports.pdf", "application/pdf");
            case "csv":
                byte[] csvBytes = exportReportsToCsv(reports).getBytes(StandardCharsets.UTF_8);
                return new ExportedReport(csvBytes, "reports.csv", "text/csv; charset=UTF-8");
            case "xlsx":
            default:
                byte[] xlsx = exportReportsToXlsx(reports);
                return new ExportedReport(xlsx, "reports.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    }

    /**
     * Exporta reportes a formato CSV
     *
     * @param reports Lista de reportes a exportar
     * @return String con contenido CSV
     */
    public String exportReportsToCsv(List<SecurityReport> reports) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < HEADERS.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(HEADERS.get(i));
        }
        sb.append("\n");

        for (SecurityReport r : reports) {
            sb.append(safe(r.getId())).append(",");
            sb.append(escapeCsv(r.getTitle())).append(",");
            sb.append(safe(r.getType())).append(",");
            sb.append(safe(r.getCreatedBy())).append(",");
            sb.append(safe(r.getOccurredAt())).append(",");
            sb.append(safe(r.getCreatedAt())).append(",");
            sb.append(safe(r.getStatus())).append(",");
            sb.append(escapeCsv(r.getDescription())).append("\n");
        }
        return sb.toString();
    }

    /**
     * Convierte objeto a string seguro (maneja valores nulos)
     *
     * @param o Objeto a convertir
     * @return String seguro (cadena vacía si es nulo)
     */
    private String safe(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * Escapa texto para formato CSV
     *
     * @param s Texto a escapar
     * @return Texto escapado para CSV
     */
    private String escapeCsv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }


    /**
     * Exporta reportes a formato XLSX con formato profesional
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo Excel
     * @throws RuntimeException Si ocurre error durante la generación
     */
    public byte[] exportReportsToXlsx(List<SecurityReport> reports) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Security Reports");

            CellStyle headerStyle = createHeaderStyle(workbook);
            createHeaderRow(sheet, headerStyle);

            populateDataRows(sheet, reports);

            autoSizeColumns(sheet, HEADERS.size());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new ReportExportException("Error generating XLSX report", e);
        }
    }

    /**
     * Crea estilo para encabezados de Excel
     *
     * @param wb Workbook de Excel
     * @return CellStyle configurado para encabezados
     */
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }


    /**
     * Crea fila de encabezados en la hoja de Excel
     *
     * @param sheet Hoja de Excel
     * @param headerStyle Estilo para encabezados
     */
    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.size(); i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(HEADERS.get(i));
            c.setCellStyle(headerStyle);
        }
    }


    /**
     * Pobla las filas de datos en la hoja de Excel
     *
     * @param sheet Hoja de Excel
     * @param reports Lista de reportes a incluir
     */
    private void populateDataRows(Sheet sheet, List<SecurityReport> reports) {
        int rowIdx = 1;
        for (SecurityReport r : reports) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(safe(r.getId()));
            row.createCell(1).setCellValue(safe(r.getTitle()));
            row.createCell(2).setCellValue(safe(r.getType()));
            row.createCell(3).setCellValue(safe(r.getCreatedBy()));
            row.createCell(4).setCellValue(safe(r.getOccurredAt()));
            row.createCell(5).setCellValue(safe(r.getCreatedAt()));
            row.createCell(6).setCellValue(safe(r.getStatus()));
            row.createCell(7).setCellValue(safe(r.getDescription()));
        }
    }

    /**
     * Auto-ajusta el ancho de las columnas en Excel
     *
     * @param sheet Hoja de Excel
     * @param cols Número de columnas a ajustar
     */
    private void autoSizeColumns(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Exporta reportes a formato PDF con tabla estructurada
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo PDF
     * @throws RuntimeException Si ocurre error durante la generación
     */
    public byte[] exportReportsToPdf(List<SecurityReport> reports) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            addPdfTitle(document);
            addPdfTable(document, reports);

            document.close();
            return out.toByteArray();
        } catch (DocumentException de) {
            throw new ReportExportException("Error generating PDF report", de);
        } catch (Exception e) {
            throw new ReportExportException("Error generating PDF report", e);
        }
    }

    /**
     * Agrega título y fecha al documento PDF
     *
     * @param document Documento PDF
     * @throws DocumentException Si ocurre error al agregar contenido
     */
    private void addPdfTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Security Reports");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Generated at: " + LocalDateTime.now().toString()));
        document.add(new Paragraph(" "));
    }

    /**
     * Agrega tabla de datos al documento PDF
     *
     * @param document Documento PDF
     * @param reports Lista de reportes a incluir en la tabla
     * @throws DocumentException Si ocurre error al agregar la tabla
     */
    private void addPdfTable(Document document, List<SecurityReport> reports) throws DocumentException {
        PdfPTable table = new PdfPTable(HEADERS.size());
        table.setWidthPercentage(100f);

        for (String h : HEADERS) {
            table.addCell(h);
        }

        for (SecurityReport r : reports) {
            table.addCell(safe(r.getId()));
            table.addCell(safe(r.getTitle()));
            table.addCell(safe(r.getType()));
            table.addCell(safe(r.getCreatedBy()));
            table.addCell(safe(r.getOccurredAt()));
            table.addCell(safe(r.getCreatedAt()));
            table.addCell(safe(r.getStatus()));
            table.addCell(safe(r.getDescription()));
        }

        document.add(table);
    }

    /**
     * Exporta reportes a bytes para Excel
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo Excel
     */
    public byte[] exportReportsToExcelBytes(List<SecurityReport> reports) {
        return exportReportsToXlsx(reports);
    }
}
