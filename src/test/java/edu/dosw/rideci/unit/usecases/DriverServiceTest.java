package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.DriverApprovedEvent;
import edu.dosw.rideci.application.events.DriverDocumentUploadedEvent;
import edu.dosw.rideci.application.events.DriverRejectedEvent;
import edu.dosw.rideci.application.port.out.DriverRepositoryPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.application.service.DriverService;
import edu.dosw.rideci.domain.model.Driver;
import edu.dosw.rideci.application.exceptions.DriverNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DriverServiceTest {

    @InjectMocks
    private DriverService service;

    @Mock
    private DriverRepositoryPort driverRepo;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AdminActionService adminActionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListBySearch() {
        Driver d1 = Driver.builder().driverId(Long.valueOf(1L)).name("Robinson").status("PENDING").build();
        Driver d2 = Driver.builder().driverId(Long.valueOf(2L)).name("Roberta").status("PENDING").build();
        when(driverRepo.searchByName("rob")).thenReturn(List.of(d1, d2));
        List<Driver> res = service.listDrivers(null, "rob", 0, 10);
        assertNotNull(res);
        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(d -> Long.valueOf(1L).equals(d.getDriverId())));;
    }

    @Test
    void shouldListByStatus() {
        Driver d = Driver.builder().driverId(Long.valueOf(3L)).name("Ana").status("PENDING").build();
        when(driverRepo.findByStatus("PENDING")).thenReturn(List.of(d));
        List<Driver> res = service.listDrivers("PENDING", null, 0, 10);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("PENDING", res.get(0).getStatus());
    }

    @Test
    void shouldListDefaultVerified() {
        Driver d = Driver.builder().driverId(Long.valueOf(4L)).name("Carlos").status("VERIFIED").build();
        when(driverRepo.findByStatus("VERIFIED")).thenReturn(List.of(d));
        List<Driver> res = service.listDrivers(null, null, 0, 10);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("VERIFIED", res.get(0).getStatus());
    }


    @Test
    void shouldGetDriverSuccess() {
        Driver d = Driver.builder().driverId(Long.valueOf(10L)).name("Found Driver").build();
        when(driverRepo.findById(Long.valueOf(10L))).thenReturn(Optional.of(d));
        Driver res = service.getDriver(Long.valueOf(10L));
        assertNotNull(res);
        assertEquals(10L, res.getDriverId());
    }

    @Test
    void shouldThrowWhenDriverNotFound() {
        when(driverRepo.findById(Long.valueOf(99L))).thenReturn(Optional.empty());
        DriverNotFoundException ex = assertThrows(DriverNotFoundException.class, () -> service.getDriver(Long.valueOf(99L)));
        assertTrue(ex.getMessage().contains("Driver not found"));
    }


    @Test
    void shouldApproveDriver() {
        Long driverId = (Long) 20L;
        Long adminId = (Long) 5L;
        Driver existing = Driver.builder()
                .driverId(driverId)
                .status("PENDING")
                .build();

        when(driverRepo.findById(driverId)).thenReturn(Optional.of(existing));
        when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0)); // return saved
        service.approveDriver(driverId, adminId);

        ArgumentCaptor<Driver> saveCaptor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepo, times(1)).save(saveCaptor.capture());
        Driver saved = saveCaptor.getValue();
        assertEquals("VERIFIED", saved.getStatus());
        assertNotNull(saved.getVerifiedAt());
        verify(adminActionService, times(1)).recordAction(eq(adminId), eq("APPROVE_DRIVER"), eq("DRIVER"), eq(String.valueOf(driverId)), anyString());
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture(), eq("admin.driver.approved"));
        Object ev = eventCaptor.getValue();
        assertTrue(ev instanceof DriverApprovedEvent);
        DriverApprovedEvent dae = (DriverApprovedEvent) ev;
        assertEquals(driverId, dae.getDriverId());
        assertEquals(adminId, dae.getAdminId());
        assertNotNull(dae.getVerifiedAt());
        assertEquals(saved.getVerifiedAt().withNano(0), dae.getVerifiedAt().withNano(0));
    }

    @Test
    void shouldRejectDriver() {
        Long driverId = (Long) 30L;
        Long adminId = (Long) 7L;
        String reason = "invalid papers";
        Driver existing = Driver.builder()
                .driverId(driverId)
                .status("PENDING")
                .build();

        when(driverRepo.findById(driverId)).thenReturn(Optional.of(existing));
        when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service.rejectDriver(driverId, adminId, reason);

        ArgumentCaptor<Driver> saveCaptor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepo, times(1)).save(saveCaptor.capture());
        Driver saved = saveCaptor.getValue();
        assertEquals("REJECTED", saved.getStatus());
        assertEquals(reason, saved.getRejectionReason());
        verify(adminActionService, times(1)).recordAction(eq(adminId), eq("REJECT_DRIVER"), eq("DRIVER"), eq(String.valueOf(driverId)), contains(reason));
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture(), eq("admin.driver.rejected"));
        Object ev = eventCaptor.getValue();
        assertTrue(ev instanceof DriverRejectedEvent);
        DriverRejectedEvent dre = (DriverRejectedEvent) ev;
        assertEquals(driverId, dre.getDriverId());
        assertEquals(adminId, dre.getAdminId());
        assertEquals(reason, dre.getReason());
        assertNotNull(dre.getRejectedAt());
    }


    @Test
    void shouldAddDocumentRefAndPublishEvent() {
        Long driverId = (Long) 40L;
        Long uploadedBy = (Long) 99L;
        String fileRef = "file-abc";
        String type = "LICENSE";

        Driver existing = Driver.builder()
                .driverId(driverId)
                .documentRefs(null)
                .build();

        when(driverRepo.findById(driverId)).thenReturn(Optional.of(existing));
        when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addDocumentRef(driverId, fileRef, type, uploadedBy);
        ArgumentCaptor<Driver> saveCaptor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepo, times(1)).save(saveCaptor.capture());
        Driver saved = saveCaptor.getValue();
        assertNotNull(saved.getDocumentRefs());
        assertTrue(saved.getDocumentRefs().contains(fileRef));
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture(), eq("admin.driver.document.uploaded"));
        Object ev = eventCaptor.getValue();
        assertTrue(ev instanceof DriverDocumentUploadedEvent);
        DriverDocumentUploadedEvent dde = (DriverDocumentUploadedEvent) ev;
        assertEquals(driverId, dde.getDriverId());
        assertEquals(fileRef, dde.getFileId());
        assertEquals(type, dde.getType());
        assertEquals(uploadedBy, dde.getUploadedBy());
        assertNotNull(dde.getUploadedAt());
    }
}
