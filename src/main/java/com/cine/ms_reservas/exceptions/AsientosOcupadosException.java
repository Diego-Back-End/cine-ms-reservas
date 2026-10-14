package com.cine.ms_reservas.exceptions;

import java.util.List;

/**
 * Se lanza cuando uno o mas asientos solicitados ya estan ocupados para una
 * funcion. El handler la traduce a HTTP 409 incluyendo los asientos en conflicto.
 */
public class AsientosOcupadosException extends RuntimeException {

    private final List<String> asientos;

    public AsientosOcupadosException(List<String> asientos) {
        super("Los siguientes asientos ya estan ocupados: " + String.join(", ", asientos));
        this.asientos = asientos;
    }

    public List<String> getAsientos() {
        return asientos;
    }
}
