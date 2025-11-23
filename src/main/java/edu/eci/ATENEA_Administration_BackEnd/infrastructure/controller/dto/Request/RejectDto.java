package edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para rechazo de conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectDto {
    private Long adminId;
    private String reason;
}
