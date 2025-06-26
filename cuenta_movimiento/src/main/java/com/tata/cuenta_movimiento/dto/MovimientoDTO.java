package com.tata.cuenta_movimiento.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la entidad Movimiento.
 * 
 * <p>Este DTO se utiliza para transferir datos de movimientos entre la capa de
 * presentación y la capa de servicio, incluyendo validaciones de entrada.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(DEPOSITO|RETIRO|TRANSFERENCIA|PAGO)$", message = "El tipo de movimiento debe ser DEPOSITO, RETIRO, TRANSFERENCIA o PAGO")
    private String tipoMovimiento;
    
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El valor debe ser mayor a 0")
    private BigDecimal valor;
    
    @NotNull(message = "El saldo es obligatorio")
    private BigDecimal saldo;
    
    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long cuentaId;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;
    
    // Constructor con parámetros básicos
    public MovimientoDTO(LocalDateTime fecha, String tipoMovimiento, BigDecimal valor, BigDecimal saldo, Long cuentaId) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuentaId = cuentaId;
    }
    
    // Constructor con descripción
    public MovimientoDTO(LocalDateTime fecha, String tipoMovimiento, BigDecimal valor, BigDecimal saldo, Long cuentaId, String descripcion) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldo = saldo;
        this.cuentaId = cuentaId;
        this.descripcion = descripcion;
    }
} 