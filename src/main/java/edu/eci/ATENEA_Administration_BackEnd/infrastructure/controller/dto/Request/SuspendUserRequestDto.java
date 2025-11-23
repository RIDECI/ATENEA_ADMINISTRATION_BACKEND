package edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de suspensión de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspendUserRequestDto {
    private Long adminId;
    private String reason;
    private String startAt;
    private String endAt;
}
