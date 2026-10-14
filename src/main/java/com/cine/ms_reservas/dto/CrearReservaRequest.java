package com.cine.ms_reservas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearReservaRequest(
        @NotNull(message = "El funcionId es obligatorio")
        Long funcionId,

        @NotEmpty(message = "Debe indicar al menos 1 asiento")
        @Size(max = 10, message = "No se pueden reservar mas de 10 asientos")
        List<@NotBlank(message = "El asiento no puede estar vacio") String> asientos,

        @NotNull(message = "El precioUnitario es obligatorio")
        @Positive(message = "El precioUnitario debe ser mayor que 0")
        Double precioUnitario
) {}
