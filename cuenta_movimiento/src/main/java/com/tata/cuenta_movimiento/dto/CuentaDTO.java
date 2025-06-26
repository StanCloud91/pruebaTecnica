package com.tata.cuenta_movimiento.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la entidad Cuenta.
 * 
 * <p>Este DTO se utiliza para transferir datos de cuentas entre la capa de
 * presentación y la capa de servicio, incluyendo validaciones de entrada.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {
    
    private Long id;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 4, max = 20, message = "El número de cuenta debe tener entre 4 y 20 caracteres")
    private String numeroCuenta;
    
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Pattern(regexp = "^(AHORROS|CORRIENTE|PLAZO_FIJO)$", message = "El tipo de cuenta debe ser AHORROS, CORRIENTE o PLAZO_FIJO")
    private String tipoCuenta;
    
    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo debe ser mayor o igual a 0")
    private BigDecimal saldo;
    
    @NotBlank(message = "El cliente es obligatorio")
    private String cliente;
    
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Constructor con parámetros básicos
    public CuentaDTO(String numeroCuenta, String tipoCuenta, BigDecimal saldo, String cliente, Boolean estado) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.cliente = cliente;
        this.estado = estado;
    }
} 