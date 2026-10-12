package com.cine.ms_reservas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaResponse(
        Long id,
        String usuarioId,
        Long funcionId,
        LocalDateTime fechaCreacion,
        BigDecimal total,
        String estado,
        List<ReservaAsientoResponse> asientos
) {}
