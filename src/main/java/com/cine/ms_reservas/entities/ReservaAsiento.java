package com.cine.ms_reservas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

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

    @Column(nullable = false, length = 10)
    @NotBlank
    private String asiento;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    private BigDecimal precio;
}
