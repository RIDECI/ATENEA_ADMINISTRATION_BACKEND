package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "institutional_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionalReport {

    @Id
    private String reportId;

    private String reportType;
    private String generatedBy;
    private LocalDateTime generationDate;
    private String dataRange;
    private String content;
    private Map<String, Object> filters;
}
