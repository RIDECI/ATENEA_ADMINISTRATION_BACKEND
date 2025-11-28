package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.TripMonitoringUseCase;

import edu.dosw.rideci.infrastructure.controller.TripController;
import edu.dosw.rideci.infrastructure.controller.dto.response.DashboardResponse;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripListItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripControllerTest {

    @InjectMocks
    private TripController controller;

    @Mock
    private TripMonitoringUseCase tripService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListTrips() {
        TripListItemDto item = new TripListItemDto();
        when(tripService.listTrips(null, null, null, 0, 20)).thenReturn(List.of(item));
        ResponseEntity<List<TripListItemDto>> res = controller.listTrips(null, null, null, 0, 20);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldActiveTrips() {
        TripListItemDto item = new TripListItemDto();
        when(tripService.getActiveTrips()).thenReturn(List.of(item));
        ResponseEntity<List<TripListItemDto>> res = controller.activeTrips();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldGetTripDetail() {
        Long id = 100L;
        TripDetailDto dto = new TripDetailDto();
        when(tripService.getTripDetail(id)).thenReturn(dto);
        ResponseEntity<TripDetailDto> res = controller.getTripDetail(id);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
    }

    @Test
    void shouldMetrics() {
        DashboardResponse dr = new DashboardResponse();
        when(tripService.getMetrics()).thenReturn(dr);
        ResponseEntity<DashboardResponse> res = controller.metrics();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dr, res.getBody());
    }
}
