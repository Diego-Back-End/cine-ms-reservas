package com.cine.ms_reservas.services;

import com.cine.ms_reservas.dto.AsientosOcupadosResponse;
import com.cine.ms_reservas.dto.CrearReservaRequest;
import com.cine.ms_reservas.dto.ReservaResponse;
import com.cine.ms_reservas.entities.EstadoReserva;
import com.cine.ms_reservas.entities.Reserva;
import com.cine.ms_reservas.entities.ReservaAsiento;
import com.cine.ms_reservas.exceptions.AccesoDenegadoException;
import com.cine.ms_reservas.exceptions.AsientosOcupadosException;
import com.cine.ms_reservas.exceptions.OperacionInvalidaException;
import com.cine.ms_reservas.exceptions.RecursoNoEncontradoException;
import com.cine.ms_reservas.repositories.ReservaRepository;
import com.cine.ms_reservas.utils.UsuarioActual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservaService {

    // Una reserva ocupa asientos mientras este PENDIENTE o CONFIRMADA.
    private static final List<EstadoReserva> ESTADOS_ACTIVOS =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioActual usuarioActual;

    /**
     * Crea una reserva en estado PENDIENTE. Valida que ninguno de los asientos
     * este ocupado para la funcion; si lo esta, lanza {@link AsientosOcupadosException}
     * (HTTP 409) indicando los asientos en conflicto.
     */
    public ReservaResponse crearReserva(CrearReservaRequest request) {
        List<String> asientos = request.asientos().stream()
                .map(a -> a.trim().toUpperCase())
                .toList();

        List<String> ocupados = reservaRepository.findAsientosOcupados(request.funcionId(), ESTADOS_ACTIVOS);
        List<String> conflictivos = asientos.stream()
                .filter(ocupados::contains)
                .distinct()
                .toList();
        if (!conflictivos.isEmpty()) {
            throw new AsientosOcupadosException(conflictivos);
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(usuarioActual.getUsuarioId());
        reserva.setFuncionId(request.funcionId());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setTotal(request.precioUnitario() * asientos.size());

        for (String asiento : asientos) {
            ReservaAsiento reservaAsiento = new ReservaAsiento();
            reservaAsiento.setReserva(reserva);
            reservaAsiento.setAsiento(asiento);
            reservaAsiento.setPrecio(request.precioUnitario());
            reserva.getAsientos().add(reservaAsiento);
        }

        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public AsientosOcupadosResponse obtenerAsientosOcupados(Long funcionId) {
        List<String> asientos = reservaRepository.findAsientosOcupados(funcionId, ESTADOS_ACTIVOS);
        return new AsientosOcupadosResponse(funcionId, asientos);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> misReservas() {
        return reservaRepository.findByUsuarioId(usuarioActual.getUsuarioId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        return toResponse(buscarPropia(id));
    }

    /**
     * Cancela una reserva propia que este PENDIENTE o CONFIRMADA. Al pasar a
     * CANCELADA sus asientos dejan de contarse como ocupados (la consulta de
     * asientos ocupados solo considera PENDIENTE y CONFIRMADA), por lo que
     * quedan liberados para futuras reservas.
     */
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = buscarPropia(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new OperacionInvalidaException("Solo se pueden cancelar reservas en estado PENDIENTE o CONFIRMADA");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return toResponse(reservaRepository.save(reserva));
    }

    private Reserva buscarPropia(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con id " + id));
        if (!reserva.getUsuarioId().equals(usuarioActual.getUsuarioId())) {
            throw new AccesoDenegadoException("No autorizado para acceder a esta reserva");
        }
        return reserva;
    }

    private ReservaResponse toResponse(Reserva reserva) {
        List<String> asientos = reserva.getAsientos().stream()
                .map(ReservaAsiento::getAsiento)
                .toList();
        return new ReservaResponse(
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getFuncionId(),
                reserva.getFechaCreacion(),
                reserva.getTotal(),
                reserva.getEstado().name(),
                asientos
        );
    }
}
