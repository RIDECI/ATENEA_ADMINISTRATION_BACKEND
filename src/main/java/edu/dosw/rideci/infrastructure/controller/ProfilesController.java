package edu.dosw.rideci.infrastructure.controller;

import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.infrastructure.controller.dto.request.SuspendUserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class ProfilesController {

    private final ManageProfileUseCase manageProfileUseCase;
    private final ProfileClientPort profileClient;

    @GetMapping
    @Operation(summary = "Listar perfiles")
    public ResponseEntity<List<Profile>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String profileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(manageProfileUseCase.listProfiles(search, profileType, page, size));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Obtener perfil por userId")
    public ResponseEntity<Profile> get(@PathVariable Long userId) {
        return manageProfileUseCase.getProfileDetails(userId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/activate")
    @Operation(summary = "Activar perfil(s)")
    public ResponseEntity<Void> activate(@PathVariable Long userId,
                                         @RequestParam Long adminId,
                                         @RequestParam(required = false) String profileType) {
        profileClient.activateProfilesForUserByType(userId, profileType);
        manageProfileUseCase.activateProfile(userId, adminId, profileType);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/suspend")
    @Operation(summary = "Suspender perfil(s)")
    public ResponseEntity<Void> suspend(@PathVariable Long userId,
                                        @RequestBody SuspendUserRequestDto dto) {
        profileClient.deactivateProfilesForUserByType(userId, dto.getProfileType());
        manageProfileUseCase.suspendProfile(userId, dto.getAdminId(), dto.getProfileType(),
                dto.getReason(), dto.getStartAt(), dto.getEndAt());
        return ResponseEntity.noContent().build();
    }
}
