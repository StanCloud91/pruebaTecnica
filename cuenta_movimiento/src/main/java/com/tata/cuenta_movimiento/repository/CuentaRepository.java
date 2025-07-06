package com.tata.cuenta_movimiento.repository;

import com.tata.cuenta_movimiento.dto.ReporteMovimientoView;
import com.tata.cuenta_movimiento.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query(value = """
                SELECT 
                    DATE_FORMAT(m.fecha, '%Y-%m-%d %H:%i:%s') as fecha,
                    p.nombre as cliente,
                    c.numero_cuenta as numeroCuenta,
                    c.tipo_cuenta as tipo,
                    CAST(CASE
                        WHEN m.tipo_movimiento = 'RETIRO' THEN m.saldo + m.valor
                        ELSE m.saldo - m.valor
                    END AS DECIMAL(15,2)) as saldoInicial,
                    CAST(IF(c.estado = 1, 1, 0) AS SIGNED) as estado,
                    CAST(CASE
                        WHEN m.tipo_movimiento = 'RETIRO' THEN m.valor * (-1)
                        ELSE m.valor
                    END AS DECIMAL(15,2)) as movimiento,
                    CAST(m.saldo AS DECIMAL(15,2)) as saldoDisponible
                FROM prueba_tecnica.cuentas c
                INNER JOIN prueba_tecnica.movimientos m ON c.id = m.cuenta_id
                INNER JOIN prueba_tecnica.personas p ON p.id = c.cliente_id
                WHERE m.fecha >= :fechaInicio
                AND m.fecha <= :fechaFin
                AND p.identificacion = :identificacion
                ORDER BY m.fecha ASC
            """, nativeQuery = true)
    List<ReporteMovimientoView> buscarMovimientosxClientexFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, @Param("identificacion") int identificacion);
} 