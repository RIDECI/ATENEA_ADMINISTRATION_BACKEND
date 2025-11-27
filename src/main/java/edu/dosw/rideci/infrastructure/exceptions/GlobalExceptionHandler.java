package edu.dosw.rideci.infrastructure.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;

/**
 * Manejador global de excepciones para la API.
 * Intercepta los errores comunes y devuelve respuestas estructuradas
 * en formato ErrorResponse.
 *
 *  @author RideECI
 *  @version 1.0
 */

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Maneja excepciones de tipo ResponseStatusException,
     * usadas comúnmente en reglas de negocio.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
        LOG.debug("Handled ResponseStatusException: {}", ex.getReason(), ex);

        int statusValue = ex.getStatusCode().value();
        String statusText = ex.getStatusCode().toString();
        String message = ex.getReason() == null ? ex.getMessage() : ex.getReason();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                statusValue,
                statusText,
                message,
                req == null ? "" : req.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }


    /**
     * Maneja errores de validación de DTOs anotados con @Valid.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        LOG.debug("Validation failed: {}", ex.getMessage());

        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        String path = "";
        if (request instanceof ServletWebRequest servletWebRequest) {
            try {
                path = servletWebRequest.getRequest().getRequestURI();
            } catch (Exception ignored) {
            }
        } else {
            path = request.getDescription(false);
        }

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                path,
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(body);
    }


    /**
     * Maneja errores de JSON mal formado.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        LOG.debug("Malformed JSON request: {}", ex.getMessage());
        String path = request instanceof ServletWebRequest sw ? sw.getRequest().getRequestURI() : request.getDescription(false);

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Malformed JSON request",
                path,
                List.of(ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(body);
    }

    /**
     * Maneja errores de validación de parámetros de URL o path variables.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        LOG.debug("Constraint violations: {}", ex.getMessage());
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toList();

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Constraint violations",
                req == null ? "" : req.getRequestURI(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja casos donde un recurso no existe.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        LOG.debug("Resource not found: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage() == null ? "Resource not found" : ex.getMessage(),
                req == null ? "" : req.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }


    /**
     * Maneja errores de acceso no autorizado.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {  //me da error AccessDeniedException
        LOG.warn("Access denied: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "Access denied",
                req == null ? "" : req.getRequestURI(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Maneja cualquier excepción no prevista.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest req) {
        String path = req == null ? "" : req.getRequestURI();
        LOG.error("Unhandled exception on {}: {}", path, ex.getMessage(), ex);

        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error",
                path,
                List.of(ex.getMessage() == null ? "unexpected error" : ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}