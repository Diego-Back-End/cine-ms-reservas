package com.cine.ms_reservas.controllers;

import com.cine.ms_reservas.dto.AsientosOcupadosResponse;
import com.cine.ms_reservas.dto.CrearReservaRequest;
import com.cine.ms_reservas.dto.ReservaResponse;
import com.cine.ms_reservas.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(@Valid @RequestBody CrearReservaRequest request) {
        ReservaResponse saved = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponse>> misReservas() {
        return ResponseEntity.ok(reservaService.misReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @GetMapping("/funcion/{funcionId}/asientos-ocupados")
    public ResponseEntity<AsientosOcupadosResponse> asientosOcupados(@PathVariable Long funcionId) {
        return ResponseEntity.ok(reservaService.obtenerAsientosOcupados(funcionId));
    }
}
