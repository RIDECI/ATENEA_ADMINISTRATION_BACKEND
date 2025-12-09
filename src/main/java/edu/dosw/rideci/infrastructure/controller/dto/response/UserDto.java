package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de usuario en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String status;
    private int suspensionCount;
    private boolean blocked;
    private String phoneNumber;
}