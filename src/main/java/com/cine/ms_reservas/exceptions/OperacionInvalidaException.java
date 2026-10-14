package com.cine.ms_reservas.exceptions;

/**
 * Se lanza cuando se intenta una operacion no permitida segun el estado actual
 * del recurso (por ejemplo cancelar una reserva ya cancelada). HTTP 400.
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String message) {
        super(message);
    }
}
