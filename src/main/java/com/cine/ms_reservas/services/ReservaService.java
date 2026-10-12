package com.cine.ms_reservas.services;

import com.cine.ms_reservas.dto.ReservaAsientoResponse;
import com.cine.ms_reservas.dto.ReservaRequest;
import com.cine.ms_reservas.dto.ReservaResponse;
import com.cine.ms_reservas.entities.EstadoReserva;
import com.cine.ms_reservas.entities.Reserva;
import com.cine.ms_reservas.entities.ReservaAsiento;
import com.cine.ms_reservas.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public ReservaResponse crear(ReservaRequest req, String usuarioId) {
        Reserva reserva = new Reserva();
        reserva.setUsuarioId(usuarioId);
        reserva.setFuncionId(req.funcionId());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;
        for (var item : req.asientos()) {
            ReservaAsiento asiento = new ReservaAsiento();
            asiento.setReserva(reserva);
            asiento.setAsiento(item.asiento().trim());
            asiento.setPrecio(item.precio());
            reserva.getAsientos().add(asiento);
            total = total.add(item.precio());
        }
        reserva.setTotal(total);

        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> misReservas(String usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id, String usuarioId) {
        Reserva reserva = buscarPropia(id, usuarioId);
        return toResponse(reserva);
    }

    public ReservaResponse cancelar(Long id, String usuarioId) {
        Reserva reserva = buscarPropia(id, usuarioId);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva ya está cancelada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return toResponse(reservaRepository.save(reserva));
    }

    private Reserva buscarPropia(Long id, String usuarioId) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        if (!reserva.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para acceder a esta reserva");
        }
        return reserva;
    }

    private ReservaResponse toResponse(Reserva r) {
        List<ReservaAsientoResponse> asientos = r.getAsientos().stream()
                .map(a -> new ReservaAsientoResponse(a.getId(), a.getAsiento(), a.getPrecio()))
                .toList();
        return new ReservaResponse(
                r.getId(),
                r.getUsuarioId(),
                r.getFuncionId(),
                r.getFechaCreacion(),
                r.getTotal(),
                r.getEstado().name(),
                asientos
        );
    }
}
