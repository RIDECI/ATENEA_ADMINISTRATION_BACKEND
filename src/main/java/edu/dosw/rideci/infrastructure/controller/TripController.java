package edu.dosw.rideci.infrastructure.controller;

import edu.dosw.rideci.application.port.in.TripMonitoringUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.dosw.rideci.infrastructure.controller.dto.response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.DashboardResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripMonitoringUseCase tripService;

    @Operation(summary = "Listar viajes con filtros opcionales")
    @GetMapping
    public ResponseEntity<List<TripListItemDto>> listTrips(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(tripService.listTrips(search, status, type, page, size));
    }

    @Operation(summary = "Obtener viajes activos en el sistema")
    @GetMapping("/active")
    public ResponseEntity<List<TripListItemDto>> activeTrips() {
        return ResponseEntity.ok(tripService.getActiveTrips());
    }

    @Operation(summary = "Obtener detalle de un viaje específico")
    @GetMapping("/{id}")
    public ResponseEntity<TripDetailDto> getTripDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tripService.getTripDetail(id));
    }

    @Operation(summary = "Obtener métricas del dashboard de viajes")
    @GetMapping("/metrics")
    public ResponseEntity<DashboardResponse> metrics() {
        return ResponseEntity.ok(tripService.getMetrics());
    }
}
