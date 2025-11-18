package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.Enum.ValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "validation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRequest {

    @Id
    private String requestId;

    private String userId;
    private String userType;
    private String driverLicense;
    private String vehiclePlate;
    private String soatDocument;
    private String insuranceDocument;
    private ValidationStatus status;
    private String validatorId;
    private LocalDateTime validationDate;
    private String comments;
}