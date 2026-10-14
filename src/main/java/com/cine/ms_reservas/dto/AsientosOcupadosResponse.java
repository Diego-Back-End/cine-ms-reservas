package com.cine.ms_reservas.dto;

import java.util.List;

public record AsientosOcupadosResponse(
        Long funcionId,
        List<String> asientos
) {}
