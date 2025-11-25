package edu.dosw.rideci.infrastructure.controller;

import edu.dosw.rideci.application.port.in.PublicationPolicyUseCase;
import edu.dosw.rideci.application.service.PublicationPolicyService;
import edu.dosw.rideci.domain.model.PolicyStrategyContext;
import edu.dosw.rideci.domain.model.PublicationPolicy;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/policies")
@RequiredArgsConstructor
public class PublicationPolicyController {

    private final PublicationPolicyUseCase service;

    @PostMapping
    @Operation(summary = "Crear una nueva política de publicación")
    public ResponseEntity<PublicationPolicy> create(@RequestBody PublicationPolicy policy) {
        return ResponseEntity.ok(service.createPolicy(policy));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una política de publicación existente")
    public ResponseEntity<PublicationPolicy> update(@PathVariable String id,
                                                    @RequestBody PublicationPolicy policy) {
        return ResponseEntity.ok(service.updatePolicy(id, policy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una política de publicación por su ID")
    public ResponseEntity<PublicationPolicy> get(@PathVariable String id) {
        return ResponseEntity.ok(service.getPolicy(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas las políticas de publicación")
    public ResponseEntity<List<PublicationPolicy>> list() {
        return ResponseEntity.ok(service.listPolicies());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una política de publicación por su ID")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allowed")
    @Operation(summary = "Verificar si está permitido publicar en un momento específico")
    public ResponseEntity<AllowedResponse> isAllowed(@RequestParam(required = false) String at,
                                                     @RequestParam(required = false) String time,
                                                     @RequestParam(required = false) Long userId,
                                                     @RequestParam(required = false) String role) {

        LocalDateTime when;
        try {
            if (at != null && !at.isBlank()) {
                when = LocalDateTime.parse(at);
            } else if (time != null && !time.isBlank()) {
                LocalTime lt = LocalTime.parse(time);
                when = LocalDateTime.now().withHour(lt.getHour()).withMinute(lt.getMinute()).withSecond(0).withNano(0);
            } else {
                when = LocalDateTime.now();
            }
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(new AllowedResponse(false, null, "invalid_datetime_format", null));
        }

        PolicyStrategyContext ctx = new PolicyStrategyContext(userId, role);
        Optional<PublicationPolicy> match = service.findMatchingPolicy(when, ctx);

        AllowedResponse resp = new AllowedResponse(
                match.isPresent(),
                match.map(PublicationPolicy::getId).orElse(null),
                match.map(PublicationPolicy::getName).orElse(null),
                when
        );
        return ResponseEntity.ok(resp);
    }

    public static record AllowedResponse(boolean allowed, String policyId, String policyName, LocalDateTime checkedAt) {}
}
