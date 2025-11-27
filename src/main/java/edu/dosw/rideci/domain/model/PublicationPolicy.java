package edu.dosw.rideci.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Modelo de dominio para políticas de publicación en RideECI
 * Define horarios permitidos para publicaciones en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationPolicy {
    private String id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean enabled;
    private String description;
    private List<DayOfWeek> allowedDays;
    private List<String> allowedRoles;
}
