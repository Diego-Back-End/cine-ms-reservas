package com.cine.ms_reservas.services;

import com.cine.ms_reservas.entities.EstadoReserva;
import com.cine.ms_reservas.entities.Reserva;
import com.cine.ms_reservas.repositories.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ReservaExpiracionTask {

    private final ReservaRepository reservaRepository;
    private final ReservaService reservaService;

    @Value("${app.reserva.tiempo-expiracion-minutos:10}")
    private int tiempoExpiracionMinutos;

    public ReservaExpiracionTask(ReservaRepository reservaRepository,
                                 ReservaService reservaService) {
        this.reservaRepository = reservaRepository;
        this.reservaService = reservaService;
    }

    /**
     * Cancela reservas PENDIENTE con fecha_creacion anterior al umbral.
     * Corre cada 1 minuto segun app.reserva.intervalo-limpieza-ms.
     */
    @Scheduled(fixedDelayString = "${app.reserva.intervalo-limpieza-ms:60000}")
    @Transactional
    public void cancelarReservasExpiradas() {
        LocalDateTime umbral = LocalDateTime.now().minusMinutes(tiempoExpiracionMinutos);
        List<Reserva> expiradas = reservaRepository.findAllByEstadoAndFechaCreacionBefore(
                EstadoReserva.PENDIENTE,
                umbral
        );
        if (expiradas.isEmpty()) {
            return;
        }
        int count = 0;
        for (Reserva reserva : expiradas) {
            try {
                reservaService.liberarAsientosReserva(reserva);
                reserva.setEstado(EstadoReserva.CANCELADA);
                reservaRepository.save(reserva);
                count++;
            } catch (Exception e) {
                log.error("No se pudo cancelar la reserva expirada id={}: {}", reserva.getId(), e.getMessage(), e);
            }
        }
        log.info("Reservas PENDIENTE expiradas canceladas: {}", count);
    }
}