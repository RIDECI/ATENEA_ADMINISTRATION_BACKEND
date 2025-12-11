package edu.dosw.rideci.domain.model.valueobjects;

import edu.dosw.rideci.domain.model.enums.ManualReason;
import lombok.*;

/**
 * Value Object para el manual de reportes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualReportRequest {
    private Long userId;
    private Long tripId;
    private Long targetId;
    private Location location;
    private String description;
    private ManualReason reason;
    private String evidence;
}