package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.domain.model.AdminAction;
import edu.dosw.rideci.infrastructure.persistence.entity.AdminActionDocument;
import edu.dosw.rideci.infrastructure.persistence.repository.AdminActionRepository;
import edu.dosw.rideci.infrastructure.persistence.repository.mapper.AdminActionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminActionServiceTest {

    @InjectMocks
    private AdminActionService service;

    @Mock
    private AdminActionRepository repo;

    @Mock
    private AdminActionMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRecordActionSaveAndReturn() {
        Long adminId = (Long) 42L;
        String action = "CREATE_POLICY";
        String targetType = "PUBLICATION_POLICY";
        String targetId = "policy-1";
        String details = "created policy X";

        when(mapper.toDocument(any(AdminAction.class))).thenAnswer(invocation -> {
            AdminAction a = invocation.getArgument(0);
            return AdminActionDocument.builder()
                    .id(null) // id not set before save
                    .adminId(a.getAdminId())
                    .action(a.getAction())
                    .targetType(a.getTargetType())
                    .targetId(a.getTargetId())
                    .details(a.getDetails())
                    .at(a.getAt())
                    .build();
        });

        AdminActionDocument savedDoc = AdminActionDocument.builder()
                .id("doc-123")
                .adminId(adminId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .details(details)
                .at(LocalDateTime.now())
                .build();

        when(repo.save(any(AdminActionDocument.class))).thenReturn(savedDoc);

        when(mapper.toDomain(savedDoc)).thenReturn(AdminAction.builder()
                .id(savedDoc.getId())
                .adminId(savedDoc.getAdminId())
                .action(savedDoc.getAction())
                .targetType(savedDoc.getTargetType())
                .targetId(savedDoc.getTargetId())
                .details(savedDoc.getDetails())
                .at(savedDoc.getAt())
                .build());

        AdminAction result = service.recordAction(adminId, action, targetType, targetId, details);
        assertNotNull(result, "result should not be null");
        assertEquals("doc-123", result.getId(), "id should come from saved document");
        assertEquals(adminId, result.getAdminId());
        assertEquals(action, result.getAction());
        assertEquals(targetType, result.getTargetType());
        assertEquals(targetId, result.getTargetId());
        assertEquals(details, result.getDetails());
        assertNotNull(result.getAt(), "timestamp must be set");

        ArgumentCaptor<AdminAction> domainCaptor = ArgumentCaptor.forClass(AdminAction.class);
        verify(mapper, times(1)).toDocument(domainCaptor.capture());
        AdminAction captured = domainCaptor.getValue();
        assertEquals(adminId, captured.getAdminId());
        assertEquals(action, captured.getAction());
        assertEquals(targetType, captured.getTargetType());
        assertEquals(targetId, captured.getTargetId());
        assertEquals(details, captured.getDetails());
        assertNotNull(captured.getAt(), "the service should set 'at' before mapping to document");

    }

    @Test
    void shouldThrowWhenRepoFails() {

        Long adminId = (Long) 7L;
        String action = "DELETE_USER";
        String targetType = "USER";
        String targetId = "u-7";
        String details = "removed user";

        when(mapper.toDocument(any(AdminAction.class))).thenReturn(
                AdminActionDocument.builder()
                        .adminId(adminId)
                        .action(action)
                        .targetType(targetType)
                        .targetId(targetId)
                        .details(details)
                        .at(LocalDateTime.now())
                        .build()
        );

        when(repo.save(any(AdminActionDocument.class))).thenThrow(new RuntimeException("db error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.recordAction(adminId, action, targetType, targetId, details));
        assertEquals("db error", ex.getMessage());

    }
}