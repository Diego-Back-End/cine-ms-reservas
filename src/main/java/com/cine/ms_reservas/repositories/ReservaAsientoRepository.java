package com.cine.ms_reservas.repositories;

import com.cine.ms_reservas.entities.ReservaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaAsientoRepository extends JpaRepository<ReservaAsiento, Long> {
    List<ReservaAsiento> findByReservaId(Long reservaId);
}
