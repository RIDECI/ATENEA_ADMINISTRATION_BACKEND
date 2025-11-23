package edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller;

import edu.eci.ATENEA_Administration_BackEnd.application.port.in.ApproveDriverUseCase;
import edu.eci.ATENEA_Administration_BackEnd.application.port.in.GetDriversUseCase;
import edu.eci.ATENEA_Administration_BackEnd.application.mapper.DriverMapper;
import edu.eci.ATENEA_Administration_BackEnd.application.service.DriverService;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Driver;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Request.DocumentRefDto;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Request.RejectDto;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Response.DriverDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class DriversController {

    private final GetDriversUseCase getDriversUseCase;
    private final ApproveDriverUseCase approveDriverUseCase;
    private final DriverMapper driverMapper;
    private final DriverService driverService;

    @Operation(summary = "Listar conductores")
    @GetMapping
    public ResponseEntity<List<DriverDto>> listDrivers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Driver> list = getDriversUseCase.listDrivers(status, search, page, size);
        return ResponseEntity.ok(driverMapper.toListDto(list));
    }

    @Operation(summary = "Aprobar conductor")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable("id") Long id,
            @RequestParam("adminId") Long adminId) {

        approveDriverUseCase.approveDriver(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechazar conductor")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable("id") Long id,
            @RequestBody RejectDto dto) {

        Long adminId = dto.getAdminId();
        approveDriverUseCase.rejectDriver(id, adminId, dto.getReason());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ver detalles del conductor")
    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriver(@PathVariable("id") Long id) {
        Driver d = getDriversUseCase.getDriver(id);
        return ResponseEntity.ok(driverMapper.toDto(d));
    }

    @PatchMapping("/{id}/documents-ref")
    public ResponseEntity<Void> addDocumentRef(@PathVariable("id") Long id,
                                               @RequestBody DocumentRefDto dto,
                                               @RequestParam(value = "uploadedBy", required=false) Long uploadedBy) {
        driverService.addDocumentRef(id, dto.getFileId(), dto.getType(), uploadedBy);
        return ResponseEntity.noContent().build();
    }
}
