package com.cine.ms_reservas.controllers;

import com.cine.ms_reservas.dto.ReservaRequest;
import com.cine.ms_reservas.dto.ReservaResponse;
import com.cine.ms_reservas.services.ReservaService;
import com.cine.ms_reservas.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(@Valid @RequestBody ReservaRequest request,
                                                        @AuthenticationPrincipal Jwt jwt) {
        String usuarioId = SecurityUtils.getUsuarioId(jwt);
        ReservaResponse saved = reservaService.crear(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponse>> misReservas(@AuthenticationPrincipal Jwt jwt) {
        String usuarioId = SecurityUtils.getUsuarioId(jwt);
        return ResponseEntity.ok(reservaService.misReservas(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> getReservaById(@PathVariable Long id,
                                                          @AuthenticationPrincipal Jwt jwt) {
        String usuarioId = SecurityUtils.getUsuarioId(jwt);
        return ResponseEntity.ok(reservaService.obtenerPorId(id, usuarioId));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(@PathVariable Long id,
                                                           @AuthenticationPrincipal Jwt jwt) {
        String usuarioId = SecurityUtils.getUsuarioId(jwt);
        return ResponseEntity.ok(reservaService.cancelar(id, usuarioId));
    }
}
