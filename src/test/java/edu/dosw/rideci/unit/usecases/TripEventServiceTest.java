package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.TravelCreatedEvent;
import edu.dosw.rideci.application.events.TravelCompletedEvent;
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
    void shouldIgnoreNullTravelIdOnCreate() {
        TravelCreatedEvent evt = mock(TravelCreatedEvent.class);
        when(evt.getTravelId()).thenReturn(null);
        service.processTripCreated(evt);

        verify(tripRepo, never()).save(any());
    }

    @Test
    void shouldProcessSundayDeparture() {
        TravelCreatedEvent evt = mock(TravelCreatedEvent.class);
        when(evt.getTravelId()).thenReturn(1L);
        LocalDate nextSunday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        when(evt.getDepartureDateAndTime()).thenReturn(nextSunday.atTime(9, 0));
        when(tripRepo.getTripById(1L)).thenReturn(null);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));
        service.processTripCreated(evt);

        verify(tripRepo, times(1)).save(any(TripMonitor.class));
    }

    @Test
    void shouldUpdateExistingTripOnCreate() {
        TravelCreatedEvent evt = mock(TravelCreatedEvent.class);
        when(evt.getTravelId()).thenReturn(10L);
        when(evt.getDriverId()).thenReturn(50L);
        when(evt.getStatus()).thenReturn(Status.ACTIVE);
        when(evt.getOrigin()).thenReturn(new Location(1.0, 2.0, "N"));
        when(evt.getDestiny()).thenReturn(new Location(3.0, 4.0, "S"));
        when(evt.getDepartureDateAndTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(evt.getEstimatedCost()).thenReturn(23.5);
        when(evt.getPassengersId()).thenReturn(List.of(101L, 102L));
        when(evt.getTravelType()).thenReturn(null);
        TripMonitor existing = new TripMonitor();
        existing.setTripId(10L);
        existing.setEstimatedCost(0.0);
        when(tripRepo.getTripById(10L)).thenReturn(existing);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));
        service.processTripCreated(evt);
        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();

        assertEquals(50L, saved.getDriverId());
        assertEquals(TripStatus.IN_PROGRESS, saved.getStatus());
        assertNotNull(saved.getStartTime());
        assertEquals(2, saved.getPassengerIds().size());
        assertEquals(23.5, saved.getEstimatedCost(), 0.0001);
    }

    @Test
    void shouldCreateNewTripOnCreate() {
        TravelCreatedEvent evt = mock(TravelCreatedEvent.class);
        when(evt.getTravelId()).thenReturn(22L);
        when(evt.getDriverId()).thenReturn(null);
        when(evt.getStatus()).thenReturn(Status.IN_COURSE);
        when(evt.getOrigin()).thenReturn(new Location(0.1, 0.2, null));
        when(evt.getDestiny()).thenReturn(null);
        when(evt.getDepartureDateAndTime()).thenReturn(null);
        when(evt.getEstimatedCost()).thenReturn(null);
        when(evt.getPassengersId()).thenReturn(List.of());
        when(evt.getTravelType()).thenReturn(null);
        when(tripRepo.getTripById(22L)).thenReturn(null);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));
        service.processTripCreated(evt);
        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();

        assertEquals(22L, saved.getTripId());
        assertEquals(TripStatus.IN_PROGRESS, saved.getStatus());
        assertNotNull(saved.getStartTime());
    }

    @Test
    void shouldIgnoreNullTravelIdOnFinish() {
        TravelCompletedEvent evt = mock(TravelCompletedEvent.class);
        when(evt.getTravelId()).thenReturn(null);
        service.processTripFinished(evt);
        verify(tripRepo, never()).save(any());
    }

    @Test
    void shouldCreateCompletedTripWhenNotExistOnFinish() {
        TravelCompletedEvent evt = mock(TravelCompletedEvent.class);
        when(evt.getTravelId()).thenReturn(300L);
        when(tripRepo.getTripById(300L)).thenReturn(null);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));
        service.processTripFinished(evt);
        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();

        assertEquals(TripStatus.COMPLETED, saved.getStatus());
        assertNotNull(saved.getEndTime());
    }

    @Test
    void shouldCompleteExistingTripOnFinish() {
        TravelCompletedEvent evt = mock(TravelCompletedEvent.class);
        when(evt.getTravelId()).thenReturn(400L);
        TripMonitor existing = new TripMonitor();
        existing.setTripId(400L);
        existing.setStatus(TripStatus.IN_PROGRESS);
        when(tripRepo.getTripById(400L)).thenReturn(existing);
        when(tripRepo.save(any(TripMonitor.class))).thenAnswer(inv -> inv.getArgument(0));

        service.processTripFinished(evt);

        ArgumentCaptor<TripMonitor> cap = ArgumentCaptor.forClass(TripMonitor.class);
        verify(tripRepo, times(1)).save(cap.capture());
        TripMonitor saved = cap.getValue();
        assertEquals(TripStatus.COMPLETED, saved.getStatus());
        assertNotNull(saved.getEndTime());
    }
}
