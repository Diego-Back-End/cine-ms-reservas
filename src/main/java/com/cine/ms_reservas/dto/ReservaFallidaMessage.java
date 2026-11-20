package com.cine.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaFallidaMessage {

    private Long reservaId;
    private String email;
    private Long funcionId;
    private String motivo;

}
