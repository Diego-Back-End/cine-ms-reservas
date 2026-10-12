package com.cine.ms_reservas.controllers;

import com.cine.ms_reservas.dto.ReservaAsientoResponse;
import com.cine.ms_reservas.services.ReservaAsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas/{reservaId}/asientos")
public class ReservaAsientoController {

    @Autowired
    private ReservaAsientoService reservaAsientoService;

    @GetMapping
    public ResponseEntity<List<ReservaAsientoResponse>> listarAsientos(@PathVariable Long reservaId) {
        return ResponseEntity.ok(reservaAsientoService.listarPorReserva(reservaId));
    }
}
