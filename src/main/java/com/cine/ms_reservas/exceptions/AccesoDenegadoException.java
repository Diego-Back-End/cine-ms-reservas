package com.cine.ms_reservas.exceptions;

/**
 * Se lanza cuando el usuario autenticado intenta acceder a un recurso que no le
 * pertenece. El handler la traduce a HTTP 403.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String message) {
        super(message);
    }
}
