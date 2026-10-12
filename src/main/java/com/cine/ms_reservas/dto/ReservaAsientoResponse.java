package com.cine.ms_reservas.dto;

import java.math.BigDecimal;

public record ReservaAsientoResponse(
        Long id,
        String asiento,
        BigDecimal precio
) {}
