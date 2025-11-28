package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.ApproveDriverUseCase;
import edu.dosw.rideci.application.port.in.GetDriversUseCase;
import edu.dosw.rideci.application.service.DriverService;
import edu.dosw.rideci.domain.model.Driver;
import edu.dosw.rideci.infrastructure.controller.DriversController;
import edu.dosw.rideci.infrastructure.controller.dto.Request.DocumentRefDto;
import edu.dosw.rideci.infrastructure.controller.dto.Request.RejectDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.DriverDto;
import edu.dosw.rideci.application.mapper.DriverMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriversControllerTest {

    @InjectMocks
    private DriversController controller;

    @Mock
    private GetDriversUseCase getDriversUseCase;

    @Mock
    private ApproveDriverUseCase approveDriverUseCase;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListDrivers() {
        Driver d = Driver.builder().driverId(1L).name("Robinson").status("PENDING").build();
        DriverDto dto = new DriverDto();
        when(getDriversUseCase.listDrivers(null, null, 0, 20)).thenReturn(List.of(d));
        when(driverMapper.toListDto(List.of(d))).thenReturn(List.of(dto));
        ResponseEntity<List<DriverDto>> res = controller.listDrivers(null, null, 0, 20);

        assertEquals(200, res.getStatusCodeValue());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldApproveDriver() {
        Long id = 5L;
        Long adminId = 2L;
        ResponseEntity<Void> res = controller.approve(id, adminId);

        assertEquals(204, res.getStatusCodeValue());
    }

    @Test
    void shouldRejectDriver() {
        Long id = 6L;
        RejectDto dto = new RejectDto();
        dto.setAdminId(3L);
        dto.setReason("bad docs");
        ResponseEntity<Void> res = controller.reject(id, dto);

        assertEquals(204, res.getStatusCodeValue());
    }

    @Test
    void shouldGetDriver() {
        Long id = 7L;
        Driver d = Driver.builder().driverId(id).name("X").build();
        DriverDto dto = new DriverDto();
        when(getDriversUseCase.getDriver(id)).thenReturn(d);
        when(driverMapper.toDto(d)).thenReturn(dto);
        ResponseEntity<DriverDto> res = controller.getDriver(id);

        assertEquals(200, res.getStatusCodeValue());
        assertSame(dto, res.getBody());
    }

    @Test
    void shouldAddDocumentRef() {
        Long id = 9L;
        DocumentRefDto dto = new DocumentRefDto();
        dto.setFileId("file-1");
        dto.setType("LICENSE");
        ResponseEntity<Void> res = controller.addDocumentRef(id, dto, 11L);

        assertEquals(204, res.getStatusCodeValue());
    }
}
