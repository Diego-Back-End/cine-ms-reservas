package com.cine.ms_reservas.services;

import com.cine.ms_reservas.dto.ReservaAsientoResponse;
import com.cine.ms_reservas.repositories.ReservaAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReservaAsientoService {

    @Autowired
    private ReservaAsientoRepository reservaAsientoRepository;

    @Transactional(readOnly = true)
    public List<ReservaAsientoResponse> listarPorReserva(Long reservaId) {
        return reservaAsientoRepository.findByReservaId(reservaId).stream()
                .map(a -> new ReservaAsientoResponse(a.getId(), a.getAsiento(), a.getPrecio()))
                .toList();
    }
}
