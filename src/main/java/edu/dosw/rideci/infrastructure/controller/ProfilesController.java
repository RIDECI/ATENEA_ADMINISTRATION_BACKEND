package edu.dosw.rideci.infrastructure.controller;

import edu.dosw.rideci.application.port.in.GetProfileUseCase;
import edu.dosw.rideci.application.port.in.GetProfilesUseCase;
import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.domain.model.Profile;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class ProfilesController {

    private final GetProfilesUseCase listUseCase;
    private final GetProfileUseCase getUseCase;
    private final ManageProfileUseCase manageUseCase;

    @Operation(summary = "Listar perfiles filtrar por type o search")
    @GetMapping
    public ResponseEntity<List<Profile>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String profileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(listUseCase.listProfiles(search, profileType, page, size));
    }

    @Operation(summary = "Obtener perfil por userId")
    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getByUserId(@PathVariable Long userId) {
        return getUseCase.getByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Activar perfil por tipo o todos")
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activateProfile(@PathVariable Long userId,
                                                @RequestParam Long adminId,
                                                @RequestParam(required = false) String profileType) {
        manageUseCase.activateProfile(userId, adminId, profileType);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar perfil por tipo o todos")
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateProfile(@PathVariable Long userId,
                                                  @RequestParam Long adminId,
                                                  @RequestParam(required = false) String profileType) {
        manageUseCase.deactivateProfile(userId, adminId, profileType);
        return ResponseEntity.noContent().build();
    }
}
