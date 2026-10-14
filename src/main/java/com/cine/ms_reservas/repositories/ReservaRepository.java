package com.cine.ms_reservas.repositories;

import com.cine.ms_reservas.entities.EstadoReserva;
import com.cine.ms_reservas.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(String usuarioId);

    /**
     * Devuelve los asientos ocupados de una funcion, considerando solo las
     * reservas en los estados indicados (PENDIENTE o CONFIRMADA).
     */
    @Query("""
            SELECT a.asiento
            FROM ReservaAsiento a
            WHERE a.reserva.funcionId = :funcionId
              AND a.reserva.estado IN :estados
            """)
    List<String> findAsientosOcupados(@Param("funcionId") Long funcionId,
                                      @Param("estados") Collection<EstadoReserva> estados);
}
