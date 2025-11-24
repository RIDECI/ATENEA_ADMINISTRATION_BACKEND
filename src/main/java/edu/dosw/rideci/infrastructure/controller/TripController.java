package edu.dosw.rideci.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.dosw.rideci.application.service.TripService;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.DashboardResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<List<TripListItemDto>> listTrips(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(tripService.listTrips(search, status, type, page, size));
    }

    @GetMapping("/active")
    public ResponseEntity<List<TripListItemDto>> activeTrips() {
        return ResponseEntity.ok(tripService.getActiveTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDetailDto> getTripDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tripService.getTripDetail(id));
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardResponse> metrics() {
        return ResponseEntity.ok(tripService.getMetrics());
    }
}
