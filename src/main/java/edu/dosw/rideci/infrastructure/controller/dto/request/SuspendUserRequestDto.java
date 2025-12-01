package edu.dosw.rideci.infrastructure.controller.dto.request;

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
    private String profileType;
    private Boolean accountOnly;
}
