package com.tata.cuenta_movimiento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una cuenta bancaria.
 * 
 * <p>Una cuenta pertenece a un cliente y puede tener múltiples movimientos.
 * Cada cuenta tiene un número único, tipo, saldo y está asociada a un cliente
 * a través de su clienteId.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 4, max = 20, message = "El número de cuenta debe tener entre 4 y 20 caracteres")
    @Column(name = "numero_cuenta", nullable = false, unique = true)
    private String numeroCuenta;
    
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Pattern(regexp = "^(AHORROS|CORRIENTE|PLAZO_FIJO)$", message = "El tipo de cuenta debe ser AHORROS, CORRIENTE o PLAZO_FIJO")
    @Column(name = "tipo_cuenta", nullable = false)
    private String tipoCuenta;
    
    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo debe ser mayor o igual a 0")
    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;
    
    @NotNull(message = "El clienteId es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;
    
    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", nullable = false)
    private Boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Movimiento> movimientos;
    
    // Constructor con parámetros básicos
    public Cuenta(String numeroCuenta, String tipoCuenta, BigDecimal saldo, Integer clienteId, Boolean estado) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.clienteId = clienteId;
        this.estado = estado;
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
} 