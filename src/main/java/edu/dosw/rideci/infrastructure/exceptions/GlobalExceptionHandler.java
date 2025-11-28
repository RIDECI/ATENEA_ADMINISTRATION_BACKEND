package edu.dosw.rideci.infrastructure.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Genera los errores correspondientes, verificando lo que se prueba.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
        LOG.debug("Handled ResponseStatusException: {}", ex.getReason(), ex);

        int statusValue = ex.getStatusCode().value();
        String statusText = ex.getStatusCode().toString();
        String message = ex.getReason() == null ? ex.getMessage() : ex.getReason();

        String path = req.getRequestURI();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                statusValue,
                statusText,
                message,
                path,
                List.of()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        LOG.debug("Constraint violations: {}", ex.getMessage());

        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toList();

        String path = req.getRequestURI();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Constraint violations",
                path,
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest req) {
        LOG.error("Unhandled exception on {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        String path = req.getRequestURI();
        String msg = Optional.ofNullable(ex.getMessage()).orElse("unexpected error");

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error",
                path,
                List.of(msg)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
