package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.mapper.TripMapper;
import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.application.service.TripService;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripServiceTest {

    @InjectMocks
    private TripService service;

    @Mock
    private TripRepositoryPort tripRepo;

    @Mock
    private TripMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListTripsByInvalidStatusReturnEmpty() {
        List<TripListItemDto> res = service.listTrips(null, "NO_SUCH_STATUS", null, 0, 10);
        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    void shouldListTripsByStatus() {
        TripMonitor t = new TripMonitor();
        when(tripRepo.findByStatus(TripStatus.IN_PROGRESS)).thenReturn(List.of(t));
        when(mapper.toListItems(List.of(t))).thenReturn(List.of(new TripListItemDto()));

        var res = service.listTrips(null, "IN_PROGRESS", null, 0, 10);
        assertEquals(1, res.size());
        verify(tripRepo, times(1)).findByStatus(TripStatus.IN_PROGRESS);
    }

    @Test
    void shouldGetActiveTrips() {
        TripMonitor t = new TripMonitor();
        when(tripRepo.findByStatus(TripStatus.IN_PROGRESS)).thenReturn(List.of(t));
        when(mapper.toListItems(List.of(t))).thenReturn(List.of(new TripListItemDto()));

        var res = service.getActiveTrips();
        assertEquals(1, res.size());
        verify(tripRepo, times(1)).findByStatus(TripStatus.IN_PROGRESS);
    }

    @Test
    void shouldGetTripDetailSuccess() {
        TripMonitor t = new TripMonitor();
        t.setTripId(55L);
        when(tripRepo.getTripById(55L)).thenReturn(t);
        TripDetailDto dto = new TripDetailDto();
        when(mapper.toDetail(t)).thenReturn(dto);

        TripDetailDto res = service.getTripDetail(55L);
        assertNotNull(res);
        verify(tripRepo, times(1)).getTripById(55L);
    }

    @Test
    void shouldThrowWhenTripNotFound() {
        when(tripRepo.getTripById(999L)).thenReturn(null);
        assertThrows(java.util.NoSuchElementException.class, () -> service.getTripDetail(999L));
    }

    @Test
    void shouldGetMetrics() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        when(tripRepo.countByStartTimeBetween(any(), any())).thenReturn(5L);
        when(tripRepo.countByStatus(TripStatus.IN_PROGRESS)).thenReturn(2L);
        when(tripRepo.sumIncomeBetween(any(), any())).thenReturn(123.45);
        when(tripRepo.sumCo2Between(any(), any())).thenReturn(7.89);

        DashboardResponse resp = service.getMetrics();
        assertNotNull(resp);
        assertEquals(5, resp.getTripsToday());
        assertEquals(2, resp.getTripsInProgress());
        assertEquals(123.45, resp.getIncome());
        assertEquals(7.89, resp.getCo2Reduced());
    }
}
