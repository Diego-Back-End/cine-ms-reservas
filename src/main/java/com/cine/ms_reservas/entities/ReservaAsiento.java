package com.cine.ms_reservas.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reserva_asientos",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_reserva_asiento_funcion_asiento",
                columnNames = {"funcion_id", "asiento_activo"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    // Asiento identificado como texto, por ejemplo "F5".
    @Column(nullable = false, length = 10)
    private String asiento;

    // Copia de la funcion de la reserva. Junto con asientoActivo forma la
    // restriccion unica que impide que dos reservas activas tomen el mismo
    // asiento en la misma funcion.
    @Column(name = "funcion_id")
    private Long funcionId;

    // Codigo del asiento mientras la reserva esta PENDIENTE o CONFIRMADA.
    // Al cancelar pasa a NULL: MySQL permite varias filas con NULL en un
    // indice unico, por lo que los asientos liberados no bloquean a nadie.
    @Column(name = "asiento_activo", length = 10)
    private String asientoActivo;

    @Column(nullable = false)
    private Double precio;
}
