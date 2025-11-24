package edu.dosw.rideci.infrastructure.controller.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para referencia de documentos en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRefDto {
    private String fileId;
    private String type;
}
