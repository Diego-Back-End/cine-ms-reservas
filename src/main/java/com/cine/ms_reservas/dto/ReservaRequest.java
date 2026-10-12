package com.cine.ms_reservas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ReservaRequest(
        @NotNull Long funcionId,
        @NotEmpty List<@Valid ReservaAsientoRequest> asientos
) {}
