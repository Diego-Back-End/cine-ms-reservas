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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reserva_asientos")
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

    @Column(nullable = false)
    private Double precio;
}
