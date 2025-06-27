package com.tata.cuenta_movimiento.repository;

import com.tata.cuenta_movimiento.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de persistencia de cuentas.
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    /**
     * Busca una cuenta por su número de cuenta.
     * 
     * @param numeroCuenta Número de cuenta a buscar
     * @return Optional con la cuenta encontrada
     */
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    /**
     * Busca todas las cuentas de un cliente específico.
     * 
     * @param clienteId ID del cliente
     * @return Lista de cuentas del cliente
     */
    List<Cuenta> findByClienteId(Integer clienteId);
    
    /**
     * Busca cuentas por tipo de cuenta.
     * 
     * @param tipoCuenta Tipo de cuenta (AHORROS, CORRIENTE, PLAZO_FIJO)
     * @return Lista de cuentas del tipo especificado
     */
    List<Cuenta> findByTipoCuenta(String tipoCuenta);
    
    /**
     * Verifica si existe una cuenta con el número especificado.
     * 
     * @param numeroCuenta Número de cuenta a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNumeroCuenta(String numeroCuenta);
    
    /**
     * Verifica si existe una cuenta con el número especificado, excluyendo una cuenta específica.
     * 
     * @param numeroCuenta Número de cuenta a verificar
     * @param id ID de la cuenta a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByNumeroCuentaAndIdNot(String numeroCuenta, Long id);
} 