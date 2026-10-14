package com.cine.ms_reservas.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaResponse(
        Long id,
        String usuarioId,
        Long funcionId,
        LocalDateTime fechaCreacion,
        Double total,
        String estado,
        List<String> asientos
) {}
