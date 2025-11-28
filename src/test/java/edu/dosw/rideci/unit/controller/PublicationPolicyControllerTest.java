package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.PublicationPolicyUseCase;
import edu.dosw.rideci.domain.model.PublicationPolicy;
import edu.dosw.rideci.domain.model.PolicyStrategyContext;
import edu.dosw.rideci.infrastructure.controller.PublicationPolicyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
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

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldUpdatePolicy() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.updatePolicy("id1", p)).thenReturn(p);
        ResponseEntity<PublicationPolicy> res = controller.update("id1", p);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldGetPolicy() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.getPolicy("id2")).thenReturn(p);
        ResponseEntity<PublicationPolicy> res = controller.get("id2");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldListPolicies() {
        PublicationPolicy p = new PublicationPolicy();
        when(service.listPolicies()).thenReturn(List.of(p));
        ResponseEntity<List<PublicationPolicy>> res = controller.list();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldDeletePolicy() {
        doNothing().when(service).deletePolicy("x");
        ResponseEntity<Void> res = controller.delete("x");

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void shouldAllowedTrue() {
        PublicationPolicy p = new PublicationPolicy();
        p.setId("pid");
        p.setName("n");
        when(service.findMatchingPolicy(any(LocalDateTime.class), any(PolicyStrategyContext.class)))
                .thenReturn(Optional.of(p));
        ResponseEntity<PublicationPolicyController.AllowedResponse> res = controller.isAllowed(null, null, 1L, "STUDENT");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(res.getBody().allowed());
        assertEquals("pid", res.getBody().policyId());
    }

    @Test
    void shouldAllowedBadRequestOnInvalidDate() {
        ResponseEntity<PublicationPolicyController.AllowedResponse> res = controller.isAllowed("bad-date", null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertFalse(res.getBody().allowed());
        assertEquals("invalid_datetime_format", res.getBody().policyName());
    }

    @Test
    void shouldAllowedWhenTimeParamProvided() {
        when(service.findMatchingPolicy(any(LocalDateTime.class), any(PolicyStrategyContext.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<PublicationPolicyController.AllowedResponse> res =
                controller.isAllowed(null, "08:30", 42L, "STUDENT");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertFalse(res.getBody().allowed());

        ArgumentCaptor<LocalDateTime> capt = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<PolicyStrategyContext> captCtx = ArgumentCaptor.forClass(PolicyStrategyContext.class);
        verify(service, times(1)).findMatchingPolicy(capt.capture(), captCtx.capture());

        LocalDateTime sent = capt.getValue();
        assertEquals(8, sent.getHour());
        assertEquals(30, sent.getMinute());

        PolicyStrategyContext ctx = captCtx.getValue();

        boolean foundUser = false;
        boolean foundRole = false;
        for (java.lang.reflect.Field f : ctx.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object v;
            try {
                v = f.get(ctx);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (v instanceof Number && ((Number) v).longValue() == 42L) foundUser = true;
            if ("STUDENT".equals(v)) foundRole = true;
        }

        assertTrue(foundUser, "expected PolicyStrategyContext to contain userId 42");
        assertTrue(foundRole, "expected PolicyStrategyContext to contain role 'STUDENT'");
    }



    @Test
    void shouldAllowedFalseWhenNoMatchingPolicyWithAtParam() {
        when(service.findMatchingPolicy(any(LocalDateTime.class), any())).thenReturn(Optional.empty());
        String at = "2025-02-10T14:15:00";
        ResponseEntity<PublicationPolicyController.AllowedResponse> res = controller.isAllowed(at, null, null, null);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertFalse(res.getBody().allowed());
        assertNull(res.getBody().policyId());
        assertNull(res.getBody().policyName());
        ArgumentCaptor<LocalDateTime> capt = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(service).findMatchingPolicy(capt.capture(), any());
        assertEquals(LocalDateTime.parse(at), capt.getValue());
    }




}
