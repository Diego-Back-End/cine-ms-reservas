package com.cine.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaConfirmadaMessage {

    private Long reservaId;
    private String usuarioId;
    private String emailUsuario;
    private Long funcionId;
    private List<String> asientos;
    private Double total;

}
