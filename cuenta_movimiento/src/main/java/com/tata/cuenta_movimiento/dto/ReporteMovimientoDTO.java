package com.tata.cuenta_movimiento.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReporteMovimientoDTO {
    private LocalDateTime fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private BigDecimal movimiento;
    private BigDecimal saldoDisponible;
} 