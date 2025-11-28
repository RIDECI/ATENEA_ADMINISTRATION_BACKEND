package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.ApproveDriverUseCase;
import edu.dosw.rideci.application.port.in.GetDriversUseCase;
import edu.dosw.rideci.application.service.DriverService;
import edu.dosw.rideci.domain.model.Driver;
import edu.dosw.rideci.infrastructure.controller.DriversController;
import edu.dosw.rideci.infrastructure.controller.dto.request.DocumentRefDto;
import edu.dosw.rideci.infrastructure.controller.dto.request.RejectDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.DriverDto;
import edu.dosw.rideci.application.mapper.DriverMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
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
        when(driverMapper.toListDto(anyList())).thenReturn(List.of(dto));
        ResponseEntity<List<DriverDto>> res = controller.listDrivers(null, null, 0, 20);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldApproveDriver() {
        Long id = 5L;
        Long adminId = 2L;
        ResponseEntity<Void> res = controller.approve(id, adminId);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void shouldRejectDriver() {
        Long id = 6L;
        RejectDto dto = new RejectDto();
        dto.setAdminId(3L);
        dto.setReason("bad docs");
        ResponseEntity<Void> res = controller.reject(id, dto);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }

    @Test
    void shouldGetDriver() {
        Long id = 7L;
        Driver d = Driver.builder().driverId(id).name("X").build();
        DriverDto dto = new DriverDto();
        when(getDriversUseCase.getDriver(id)).thenReturn(d);
        when(driverMapper.toDto(any())).thenReturn(dto);
        ResponseEntity<DriverDto> res = controller.getDriver(id);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
    }

    @Test
    void shouldAddDocumentRef() {
        Long id = 9L;
        DocumentRefDto dto = new DocumentRefDto();
        dto.setFileId("file-1");
        dto.setType("LICENSE");
        ResponseEntity<Void> res = controller.addDocumentRef(id, dto, 11L);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
