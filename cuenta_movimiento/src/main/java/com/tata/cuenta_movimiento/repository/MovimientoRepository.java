package com.tata.cuenta_movimiento.repository;

import com.tata.cuenta_movimiento.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones de persistencia de movimientos.
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    /**
     * Busca todos los movimientos de una cuenta específica.
     * 
     * @param cuentaId ID de la cuenta
     * @return Lista de movimientos de la cuenta
     */
    List<Movimiento> findByCuentaId(Long cuentaId);
    
    /**
     * Busca movimientos por tipo de movimiento.
     * 
     * @param tipoMovimiento Tipo de movimiento (DEPOSITO, RETIRO, TRANSFERENCIA, PAGO)
     * @return Lista de movimientos del tipo especificado
     */
    List<Movimiento> findByTipoMovimiento(String tipoMovimiento);
    
    /**
     * Busca movimientos de una cuenta por tipo de movimiento.
     * 
     * @param cuentaId ID de la cuenta
     * @param tipoMovimiento Tipo de movimiento
     * @return Lista de movimientos filtrados
     */
    List<Movimiento> findByCuentaIdAndTipoMovimiento(Long cuentaId, String tipoMovimiento);
    
    /**
     * Busca movimientos de una cuenta en un rango de fechas.
     * 
     * @param cuentaId ID de la cuenta
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de movimientos en el rango de fechas
     */
    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Busca movimientos por tipo de movimiento en un rango de fechas.
     * 
     * @param tipoMovimiento Tipo de movimiento
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de movimientos filtrados
     */
    List<Movimiento> findByTipoMovimientoAndFechaBetween(String tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFin);
} 