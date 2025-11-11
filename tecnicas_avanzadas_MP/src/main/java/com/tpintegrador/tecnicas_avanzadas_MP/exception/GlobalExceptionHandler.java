package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ApiError error = buildError(status, ex.getReason(), request.getRequestURI(), null);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        ApiError error = buildError(HttpStatus.BAD_REQUEST, "Error de validación", request.getRequestURI(), details);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.FORBIDDEN, "Acceso denegado", request.getRequestURI(),
                Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ApiError error = buildError(HttpStatus.BAD_REQUEST, "Solicitud mal formada",
                request.getDescription(false).replace("uri=", ""),
                Collections.singletonList(ex.getLocalizedMessage()));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor",
                request.getRequestURI(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = buildError(HttpStatus.BAD_REQUEST, "Error de validación",
                request.getDescription(false).replace("uri=", ""), details);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = buildError(HttpStatus.BAD_REQUEST, "Error de validación",
                request.getRequestURI(), details);
        return ResponseEntity.badRequest().body(apiError);
    }

    private ApiError buildError(HttpStatus status, String message, String path, List<String> details) {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                OffsetDateTime.now(),
                Objects.requireNonNullElse(details, Collections.emptyList())
        );
    }
}

