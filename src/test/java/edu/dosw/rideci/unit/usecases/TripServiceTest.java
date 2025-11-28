package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.mapper.TripMapper;
import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.application.service.TripService;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

    @Test
    void shouldReturnEmpty_whenStatusIsInvalid() {
        String invalidStatus = "not_a_status";
        var res = service.listTrips(null, invalidStatus, null, 0, 10);
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(tripRepo, never()).findByStatus(any());
        verify(tripRepo, never()).findAllPaged(anyInt(), anyInt());
    }

    @Test
    void shouldReturnMappedList_whenStatusProvided() {
        TripMonitor t = new TripMonitor(); // simple stub object
        List<TripMonitor> monitors = List.of(t);
        TripListItemDto dto = new TripListItemDto();
        List<TripListItemDto> mapped = List.of(dto);
        when(tripRepo.findByStatus(TripStatus.IN_PROGRESS)).thenReturn(monitors);
        when(mapper.toListItems(monitors)).thenReturn(mapped);
        var res = service.listTrips(null, "in_progress", null, 0, 10);
        assertSame(mapped, res);
        verify(tripRepo, times(1)).findByStatus(TripStatus.IN_PROGRESS);
        verify(mapper, times(1)).toListItems(monitors);
        verify(tripRepo, never()).findAllPaged(anyInt(), anyInt());
    }

    @Test
    void shouldReturnPagedMapping_whenNoStatus() {
        TripMonitor t = new TripMonitor();
        List<TripMonitor> page = List.of(t);
        TripListItemDto dto = new TripListItemDto();
        List<TripListItemDto> mapped = List.of(dto);
        when(tripRepo.findAllPaged(0, 20)).thenReturn(page);
        when(mapper.toListItems(page)).thenReturn(mapped);
        var res = service.listTrips(null, null, null, 0, 20);

        assertSame(mapped, res);
        verify(tripRepo, times(1)).findAllPaged(0, 20);
        verify(mapper, times(1)).toListItems(page);
    }
}
