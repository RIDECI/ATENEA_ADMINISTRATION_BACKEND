package edu.dosw.rideci.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value Object para insignias en RideECI
 * Representa logros o reconocimientos obtenidos por usuarios
 *
 * @author RideECI
 * @version 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Badge {

    private String name;
    private String pathImageBlackAndWhite;
    private String pathImageColor;
    private String description;
    private boolean isActive;

}
