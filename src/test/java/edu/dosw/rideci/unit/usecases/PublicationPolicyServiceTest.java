package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.PublicationPolicyCreatedEvent;
import edu.dosw.rideci.application.events.PublicationPolicyDeletedEvent;
import edu.dosw.rideci.application.events.PublicationPolicyUpdatedEvent;
import edu.dosw.rideci.application.service.PublicationPolicyService;
import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.application.port.out.PublicationPolicyRepositoryPort;
import edu.dosw.rideci.domain.model.PublicationPolicy;
import edu.dosw.rideci.infrastructure.persistence.Entity.PublicationPolicyDocument;
import edu.dosw.rideci.infrastructure.persistence.Repository.mapper.PublicationPolicyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicationPolicyServiceTest {

    @InjectMocks
    private PublicationPolicyService service;

    @Mock
    private PublicationPolicyRepositoryPort repo;

    @Mock
    private PublicationPolicyMapper mapper;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AdminActionService adminActionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreatePolicy() {
        PublicationPolicy input = PublicationPolicy.builder()
                .name("Morning Only")
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(9, 0))
                .enabled(true)
                .description("morning trips")
                .build();

        PublicationPolicyDocument docBefore = PublicationPolicyDocument.builder()
                .id(null)
                .name(input.getName())
                .startTime(input.getStartTime())
                .endTime(input.getEndTime())
                .enabled(input.isEnabled())
                .description(input.getDescription())
                .build();

        PublicationPolicyDocument savedDoc = PublicationPolicyDocument.builder()
                .id("policy-1")
                .name(input.getName())
                .startTime(input.getStartTime())
                .endTime(input.getEndTime())
                .enabled(input.isEnabled())
                .description(input.getDescription())
                .build();

        PublicationPolicy returnedDomain = PublicationPolicy.builder()
                .id("policy-1")
                .name(input.getName())
                .startTime(input.getStartTime())
                .endTime(input.getEndTime())
                .enabled(input.isEnabled())
                .description(input.getDescription())
                .build();

        when(mapper.toDocument(input)).thenReturn(docBefore);
        when(repo.save(docBefore)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(returnedDomain);

        PublicationPolicy result = service.createPolicy(input);

        assertNotNull(result);
        assertEquals("policy-1", result.getId());
        assertEquals("Morning Only", result.getName());

        verify(mapper, times(1)).toDocument(input);
        verify(repo, times(1)).save(docBefore);
        verify(mapper, times(1)).toDomain(savedDoc);
        verify(eventPublisher, times(1)).publish(any(PublicationPolicyCreatedEvent.class), eq("admin.policy.created"));
        verify(adminActionService, atMost(1)).recordAction(any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldUpdatePolicySuccess() {
        String id = "policy-2";
        PublicationPolicyDocument existing = PublicationPolicyDocument.builder()
                .id(id)
                .name("Old")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .enabled(true)
                .description("old")
                .build();

        PublicationPolicy update = PublicationPolicy.builder()
                .name("New name")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .enabled(false)
                .description("updated")
                .allowedDays(List.of())
                .allowedRoles(List.of("STUDENT"))
                .build();

        PublicationPolicyDocument saved = PublicationPolicyDocument.builder()
                .id(id)
                .name(update.getName())
                .startTime(update.getStartTime())
                .endTime(update.getEndTime())
                .enabled(update.isEnabled())
                .description(update.getDescription())
                .allowedDays(null)
                .allowedRoles(update.getAllowedRoles())
                .build();

        PublicationPolicy returnedDomain = PublicationPolicy.builder()
                .id(id)
                .name(update.getName())
                .startTime(update.getStartTime())
                .endTime(update.getEndTime())
                .enabled(update.isEnabled())
                .description(update.getDescription())
                .allowedRoles(update.getAllowedRoles())
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(returnedDomain);

        PublicationPolicy result = service.updatePolicy(id, update);

        assertNotNull(result);
        assertEquals("New name", result.getName());
        assertFalse(result.isEnabled());

        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existing);
        verify(mapper, times(1)).toDomain(saved);
        verify(eventPublisher, times(1)).publish(any(PublicationPolicyUpdatedEvent.class), eq("admin.policy.updated"));
    }

    @Test
    void shouldThrowWhenUpdatePolicyNotFound() {
        String id = "nope";
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.updatePolicy(id, PublicationPolicy.builder().build()));
        verify(repo, times(1)).findById(id);
        verify(repo, never()).save(any());
        verify(eventPublisher, never()).publish(any(), anyString());
    }

    @Test
    void shouldGetPolicy() {
        String id = "policy-3";
        PublicationPolicyDocument doc = PublicationPolicyDocument.builder()
                .id(id)
                .name("p3")
                .enabled(true)
                .build();

        PublicationPolicy domain = PublicationPolicy.builder()
                .id(id)
                .name("p3")
                .enabled(true)
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        PublicationPolicy res = service.getPolicy(id);

        assertNotNull(res);
        assertEquals("p3", res.getName());
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toDomain(doc);
    }

    @Test
    void shouldListPolicies() {
        PublicationPolicyDocument d1 = PublicationPolicyDocument.builder().id("a").name("a").enabled(true).build();
        PublicationPolicyDocument d2 = PublicationPolicyDocument.builder().id("b").name("b").enabled(true).build();

        PublicationPolicy p1 = PublicationPolicy.builder().id("a").name("a").enabled(true).build();
        PublicationPolicy p2 = PublicationPolicy.builder().id("b").name("b").enabled(true).build();

        when(repo.findAll()).thenReturn(List.of(d1, d2));
        when(mapper.toDomain(d1)).thenReturn(p1);
        when(mapper.toDomain(d2)).thenReturn(p2);

        var res = service.listPolicies();

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    void shouldDeletePolicyAndPublishEvent() {
        String id = "policy-delete";
        PublicationPolicyDocument doc = PublicationPolicyDocument.builder()
                .id(id)
                .name("ToDelete")
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(doc));

        service.deletePolicy(id);

        verify(repo, times(1)).deleteById(id);
        verify(eventPublisher, times(1)).publish(any(PublicationPolicyDeletedEvent.class), eq("admin.policy.deleted"));
        verify(adminActionService, atMost(1)).recordAction(any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnEmptyWhenNoMatchingPolicy() {
        when(repo.findAll()).thenReturn(List.of());

        var opt = service.findMatchingPolicy(null);

        assertTrue(opt.isEmpty());
        assertFalse(service.isAllowedAt(null));
        verify(repo, times(2)).findAll();
    }
}
