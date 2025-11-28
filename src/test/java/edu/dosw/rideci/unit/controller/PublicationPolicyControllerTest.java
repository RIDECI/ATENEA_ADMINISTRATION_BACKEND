package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.PublicationPolicyUseCase;
import edu.dosw.rideci.domain.model.PublicationPolicy;
import edu.dosw.rideci.domain.model.PolicyStrategyContext;
import edu.dosw.rideci.infrastructure.controller.PublicationPolicyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicationPolicyControllerTest {

    @InjectMocks
    private PublicationPolicyController controller;

    @Mock
    private PublicationPolicyUseCase service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreatePolicy() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.createPolicy(p)).thenReturn(p);
        ResponseEntity<PublicationPolicy> res = controller.create(p);

        assertEquals(200, res.getStatusCodeValue());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldUpdatePolicy() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.updatePolicy("id1", p)).thenReturn(p);
        ResponseEntity<PublicationPolicy> res = controller.update("id1", p);

        assertEquals(200, res.getStatusCodeValue());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldGetPolicy() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.getPolicy("id2")).thenReturn(p);
        ResponseEntity<PublicationPolicy> res = controller.get("id2");

        assertEquals(200, res.getStatusCodeValue());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldListPolicies() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.listPolicies()).thenReturn(List.of(p));
        ResponseEntity<List<PublicationPolicy>> res = controller.list();

        assertEquals(200, res.getStatusCodeValue());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldDeletePolicy() {
        doNothing().when(service).deletePolicy("x");
        ResponseEntity<Void> res = controller.delete("x");

        assertEquals(204, res.getStatusCodeValue());
    }

    @Test
    void shouldAllowedTrue() {
        LocalDateTime now = LocalDateTime.now();
        PublicationPolicy p = new PublicationPolicy();
        p.setId("pid");
        p.setName("n");
        when(service.findMatchingPolicy(any(LocalDateTime.class), any(PolicyStrategyContext.class)))
                .thenReturn(Optional.of(p));
        ResponseEntity<PublicationPolicyController.AllowedResponse> res = controller.isAllowed(null, null, 1L, "STUDENT");

        assertEquals(200, res.getStatusCodeValue());
        assertTrue(res.getBody().allowed());
        assertEquals("pid", res.getBody().policyId());
    }

    @Test
    void shouldAllowedBadRequestOnInvalidDate() {
        ResponseEntity<PublicationPolicyController.AllowedResponse> res = controller.isAllowed("bad-date", null, null, null);

        assertEquals(400, res.getStatusCodeValue());
        assertFalse(res.getBody().allowed());
        assertEquals("invalid_datetime_format", res.getBody().policyName());
    }
}
