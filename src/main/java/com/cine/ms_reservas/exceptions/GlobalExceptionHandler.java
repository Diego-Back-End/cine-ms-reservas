package com.cine.ms_reservas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce las excepciones de la aplicacion a respuestas JSON con un mensaje
 * claro para los codigos 400, 403, 404 y 409.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            detalles.put(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Error de validacion en la peticion", detalles);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion es invalido o ilegible", null);
    }

    @ExceptionHandler({AccesoDenegadoException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccesoDenegado(RuntimeException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleOperacionInvalida(OperacionInvalidaException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(AsientosOcupadosException.class)
    public ResponseEntity<Map<String, Object>> handleAsientosOcupados(AsientosOcupadosException ex) {
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("asientos", ex.getAsientos());
        return build(HttpStatus.CONFLICT, ex.getMessage(), detalles);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado en el servidor", null);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, Object detalles) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (detalles != null) {
            body.put("detalles", detalles);
        }
        return ResponseEntity.status(status).body(body);
    }
}
