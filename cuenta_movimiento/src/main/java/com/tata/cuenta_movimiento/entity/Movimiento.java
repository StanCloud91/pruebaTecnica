package com.tata.cuenta_movimiento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un movimiento bancario.
 * 
 * <p>Un movimiento pertenece a una cuenta específica y registra las transacciones
 * realizadas en esa cuenta. Cada movimiento tiene una fecha, tipo, valor y
 * el saldo resultante después de la transacción.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(DEPOSITO|RETIRO|TRANSFERENCIA|PAGO)$", message = "El tipo de movimiento debe ser DEPOSITO, RETIRO, TRANSFERENCIA o PAGO")
    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento;
    
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El valor debe ser mayor o igual a 0")
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    
    @NotNull(message = "El saldo es obligatorio")
    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
    
    @Column(name = "descripcion")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;
    
    // Constructor con parámetros básicos
    public Movimiento(LocalDateTime fecha, String tipoMovimiento, BigDecimal valor, BigDecimal saldo, Cuenta cuenta) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuenta = cuenta;
    }
    
    // Constructor con descripción
    public Movimiento(LocalDateTime fecha, String tipoMovimiento, BigDecimal valor, BigDecimal saldo, Cuenta cuenta, String descripcion) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuenta = cuenta;
        this.descripcion = descripcion;
    }
} 