package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.command.TravelCreatedCommand;
import edu.dosw.rideci.application.events.command.TravelCompletedCommand;
import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.application.service.TripEventService;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.domain.model.enums.Status;
import edu.dosw.rideci.domain.model.valueobjects.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripEventServiceTest {

    @InjectMocks
    private TripEventService service;

    @Mock
    private TripRepositoryPort tripRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldIgnoreNullOrMissingTravelIdOnCreate() {
        TravelCreatedCommand cmd = mock(TravelCreatedCommand.class);
        when(cmd.getTravelId()).thenReturn(null);

        service.processTripCreated(cmd);

        verify(tripRepo, never()).save(any());
    }

    @Test
    void shouldRejectSundayDeparture() {
        TravelCreatedCommand cmd = mock(TravelCreatedCommand.class);
        when(cmd.getTravelId()).thenReturn(1L);
        LocalDate nextSunday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        when(cmd.getDepartureDateAndTime()).thenReturn(nextSunday.atTime(9, 0));

        service.processTripCreated(cmd);

        verify(tripRepo, never()).save(any());
    }

    @Test
    void shouldUpdateExistingTripOnCreate() {
        TravelCreatedCommand cmd = mock(TravelCreatedCommand.class);
        when(cmd.getTravelId()).thenReturn(10L);
        when(cmd.getDriverId()).thenReturn(50L);
        when(cmd.getState()).thenReturn(Status.ACTIVE);
        when(cmd.getOrigin()).thenReturn(new Location(1.0, 2.0, "N"));
        when(cmd.getDestiny()).thenReturn(new Location(3.0, 4.0, "S"));
        when(cmd.getDepartureDateAndTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(cmd.getEstimatedCost()).thenReturn(23.5);
        when(cmd.getCo2Saved()).thenReturn(0.7);
        when(cmd.getPassengersId()).thenReturn(List.of(101L, 102L));
        when(cmd.getTravelType()).thenReturn(null);

        TripMonitor existing = new TripMonitor();
        existing.setTripId(10L);
        existing.setEstimatedCost(0.0);
        existing.setCo2Saved(0.0);

        when(tripRepo.getTripById(10L)).thenReturn(existing);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));

        service.processTripCreated(cmd);

        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();
        assertEquals(50L, saved.getDriverId());
        assertEquals(TripStatus.IN_PROGRESS, saved.getStatus());
        assertNotNull(saved.getStartTime());
        assertEquals(2, saved.getPassengerIds().size());
        assertEquals(23.5, saved.getEstimatedCost());
        assertEquals(0.7, saved.getCo2Saved());
    }

    @Test
    void shouldCreateNewTripOnCreateWhenNotExist() {
        TravelCreatedCommand cmd = mock(TravelCreatedCommand.class);
        when(cmd.getTravelId()).thenReturn(22L);
        when(cmd.getDriverId()).thenReturn(null);
        when(cmd.getState()).thenReturn(Status.IN_COURSE);
        when(cmd.getOrigin()).thenReturn(new Location(0.1, 0.2, null));
        when(cmd.getDestiny()).thenReturn(null);
        when(cmd.getDepartureDateAndTime()).thenReturn(null);
        when(cmd.getEstimatedCost()).thenReturn(null);
        when(cmd.getCo2Saved()).thenReturn(null);
        when(cmd.getPassengersId()).thenReturn(List.of());
        when(cmd.getTravelType()).thenReturn(null);

        when(tripRepo.getTripById(22L)).thenReturn(null);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));

        service.processTripCreated(cmd);

        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();
        assertEquals(22L, saved.getTripId());
        assertEquals(TripStatus.IN_PROGRESS, saved.getStatus());
        assertNotNull(saved.getStartTime());
    }

    @Test
    void shouldIgnoreNullOnFinish() {
        TravelCompletedCommand cmd = mock(TravelCompletedCommand.class);
        when(cmd.getTravelId()).thenReturn(null);

        service.processTripFinished(cmd);

        verify(tripRepo, never()).save(any());
    }

    @Test
    void shouldCreateCompletedTripWhenNotExistOnFinish() {
        TravelCompletedCommand cmd = mock(TravelCompletedCommand.class);
        when(cmd.getTravelId()).thenReturn(300L);

        when(tripRepo.getTripById(300L)).thenReturn(null);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));

        service.processTripFinished(cmd);

        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();
        assertEquals(TripStatus.COMPLETED, saved.getStatus());
        assertNotNull(saved.getEndTime());
    }

    @Test
    void shouldCompleteExistingTripOnFinish() {
        TravelCompletedCommand cmd = mock(TravelCompletedCommand.class);
        when(cmd.getTravelId()).thenReturn(400L);

        TripMonitor existing = new TripMonitor();
        existing.setTripId(400L);
        existing.setStatus(TripStatus.IN_PROGRESS);

        when(tripRepo.getTripById(400L)).thenReturn(existing);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));

        service.processTripFinished(cmd);

        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();
        assertEquals(TripStatus.COMPLETED, saved.getStatus());
        assertNotNull(saved.getEndTime());
    }
}
