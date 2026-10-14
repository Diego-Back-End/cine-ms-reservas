package com.cine.ms_reservas.exceptions;

/**
 * Se lanza cuando un recurso solicitado no existe. El handler la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
