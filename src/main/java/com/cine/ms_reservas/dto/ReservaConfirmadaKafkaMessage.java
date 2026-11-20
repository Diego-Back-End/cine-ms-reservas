package com.cine.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaConfirmadaKafkaMessage {

    private Long reservaId;
    private String email;
    private Long funcionId;
    private List<String> asientos;
    private Double total;
    private LocalDateTime fecha;

}
