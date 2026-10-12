package com.cine.ms_reservas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ReservaAsientoRequest(
        @NotBlank String asiento,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precio
) {}
