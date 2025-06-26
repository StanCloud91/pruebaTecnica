package com.tata.cuenta_movimiento.service;

import com.tata.cuenta_movimiento.dto.MovimientoDTO;
import com.tata.cuenta_movimiento.dto.MovimientoOperacionDTO;
import com.tata.cuenta_movimiento.entity.Cuenta;
import com.tata.cuenta_movimiento.entity.Movimiento;
import com.tata.cuenta_movimiento.exception.InsufficientFundsException;
import com.tata.cuenta_movimiento.exception.ResourceNotFoundException;
import com.tata.cuenta_movimiento.repository.CuentaRepository;
import com.tata.cuenta_movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de movimientos bancarios.
 * 
 * <p>Esta clase contiene toda la lógica de negocio relacionada con la gestión
 * de movimientos, incluyendo operaciones CRUD, validaciones de fondos y
 * actualización automática de saldos de cuentas.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Service
@RequiredArgsConstructor
public class MovimientoService {
    
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    
    /**
     * Obtiene todos los movimientos registrados en el sistema.
     * 
     * @return Lista de todos los movimientos convertidos a DTOs
     */
    public List<MovimientoDTO> getAllMovimientos() {
        List<Movimiento> movimientos = movimientoRepository.findAll();
        return movimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un movimiento específico por su ID.
     * 
     * @param id ID del movimiento
     * @return DTO del movimiento encontrado
     * @throws ResourceNotFoundException si el movimiento no existe
     */
    public MovimientoDTO getMovimientoById(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
        return convertToDTO(movimiento);
    }
    
    /**
     * Obtiene todos los movimientos de una cuenta específica.
     * 
     * @param cuentaId ID de la cuenta
     * @return Lista de movimientos de la cuenta
     */
    public List<MovimientoDTO> getMovimientosByCuentaId(Long cuentaId) {
        List<Movimiento> movimientos = movimientoRepository.findByCuentaId(cuentaId);
        return movimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todos los movimientos de un tipo específico.
     * 
     * @param tipoMovimiento Tipo de movimiento (DEPOSITO, RETIRO, TRANSFERENCIA, PAGO)
     * @return Lista de movimientos del tipo especificado
     */
    public List<MovimientoDTO> getMovimientosByTipo(String tipoMovimiento) {
        List<Movimiento> movimientos = movimientoRepository.findByTipoMovimiento(tipoMovimiento);
        return movimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene movimientos de una cuenta por tipo de movimiento.
     * 
     * @param cuentaId ID de la cuenta
     * @param tipoMovimiento Tipo de movimiento
     * @return Lista de movimientos filtrados
     */
    public List<MovimientoDTO> getMovimientosByCuentaIdAndTipo(Long cuentaId, String tipoMovimiento) {
        List<Movimiento> movimientos = movimientoRepository.findByCuentaIdAndTipoMovimiento(cuentaId, tipoMovimiento);
        return movimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene movimientos de una cuenta en un rango de fechas.
     * 
     * @param cuentaId ID de la cuenta
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de movimientos en el rango de fechas
     */
    public List<MovimientoDTO> getMovimientosByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Movimiento> movimientos = movimientoRepository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin);
        return movimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuevo movimiento y actualiza el saldo de la cuenta.
     * 
     * @param movimientoDTO Datos del movimiento a crear
     * @return DTO del movimiento creado con ID asignado
     * @throws ResourceNotFoundException si la cuenta no existe
     * @throws InsufficientFundsException si no hay fondos suficientes para retiros
     */
    @Transactional
    public MovimientoDTO createMovimiento(MovimientoDTO movimientoDTO) {
        // Obtener la cuenta para validar saldo y actualizarlo
        Cuenta cuenta = cuentaRepository.findById(movimientoDTO.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", movimientoDTO.getCuentaId()));
        // Validar fondos para retiros usando el saldo real de la cuenta
        if (esRetiro(movimientoDTO.getTipoMovimiento())) {
            if (cuenta.getSaldo().compareTo(movimientoDTO.getValor()) < 0) {
                throw new InsufficientFundsException(
                    cuenta.getNumeroCuenta(),
                    cuenta.getSaldo().toString(),
                    movimientoDTO.getValor().toString()
                );
            }
        }
        // Calcular nuevo saldo
        BigDecimal nuevoSaldo = calcularNuevoSaldo(cuenta.getSaldo(), movimientoDTO.getTipoMovimiento(), movimientoDTO.getValor());
        // Crear el movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);
        movimiento.setDescripcion(movimientoDTO.getDescripcion());
        Movimiento savedMovimiento = movimientoRepository.save(movimiento);
        // Actualizar saldo de la cuenta
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
        return convertToDTO(savedMovimiento);
    }
    
    /**
     * Actualiza completamente los datos de un movimiento existente.
     * 
     * @param id ID del movimiento a actualizar
     * @param movimientoDTO Nuevos datos del movimiento
     * @return DTO del movimiento actualizado
     * @throws ResourceNotFoundException si el movimiento no existe
     */
    public MovimientoDTO updateMovimiento(Long id, MovimientoDTO movimientoDTO) {
        Movimiento existingMovimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
        
        // Actualizar campos
        existingMovimiento.setFecha(movimientoDTO.getFecha());
        existingMovimiento.setTipoMovimiento(movimientoDTO.getTipoMovimiento());
        existingMovimiento.setValor(movimientoDTO.getValor());
        existingMovimiento.setSaldo(movimientoDTO.getSaldo());
        existingMovimiento.setDescripcion(movimientoDTO.getDescripcion());
        
        Movimiento updatedMovimiento = movimientoRepository.save(existingMovimiento);
        return convertToDTO(updatedMovimiento);
    }
    
    /**
     * Elimina permanentemente un movimiento del sistema.
     * 
     * @param id ID del movimiento a eliminar
     * @throws ResourceNotFoundException si el movimiento no existe
     */
    public void deleteMovimiento(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento", "id", id);
        }
        movimientoRepository.deleteById(id);
    }
    
    /**
     * Verifica si un tipo de movimiento es un retiro.
     * 
     * @param tipoMovimiento Tipo de movimiento
     * @return true si es un retiro, false en caso contrario
     */
    private boolean esRetiro(String tipoMovimiento) {
        return "RETIRO".equals(tipoMovimiento) || "PAGO".equals(tipoMovimiento);
    }
    
    /**
     * Calcula el nuevo saldo basado en el tipo de movimiento.
     * 
     * @param saldoActual Saldo actual de la cuenta
     * @param tipoMovimiento Tipo de movimiento
     * @param valor Valor del movimiento
     * @return Nuevo saldo calculado
     */
    private BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, String tipoMovimiento, BigDecimal valor) {
        switch (tipoMovimiento) {
            case "DEPOSITO":
                return saldoActual.add(valor);
            case "RETIRO":
            case "PAGO":
                return saldoActual.subtract(valor);
            case "TRANSFERENCIA":
                // Para transferencias, asumimos que es una salida de fondos
                return saldoActual.subtract(valor);
            default:
                return saldoActual;
        }
    }
    
    /**
     * Convierte una entidad Movimiento a su correspondiente DTO.
     * 
     * @param movimiento Entidad Movimiento a convertir
     * @return DTO correspondiente con los datos de la entidad
     */
    private MovimientoDTO convertToDTO(Movimiento movimiento) {
        MovimientoDTO dto = new MovimientoDTO();
        BeanUtils.copyProperties(movimiento, dto);
        if (movimiento.getCuenta() != null) {
            dto.setCuentaId(movimiento.getCuenta().getId());
        }
        return dto;
    }
    
    /**
     * Convierte un DTO Movimiento a su correspondiente entidad.
     * 
     * @param dto DTO Movimiento a convertir
     * @return Entidad correspondiente con los datos del DTO
     */
    private Movimiento convertToEntity(MovimientoDTO dto) {
        Movimiento movimiento = new Movimiento();
        BeanUtils.copyProperties(dto, movimiento);
        return movimiento;
    }

    public MovimientoDTO convertirOperacionAMovimiento(MovimientoOperacionDTO operacionDTO) {
        // Buscar cuenta por número de cuenta
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(operacionDTO.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "numeroCuenta", operacionDTO.getNumeroCuenta()));

        // Analizar el campo movimiento para obtener tipo y valor
        String movimiento = operacionDTO.getMovimiento().toUpperCase();
        String tipoMovimiento;
        Double valor;
        if (movimiento.contains("RETIRO")) {
            tipoMovimiento = "RETIRO";
            valor = extraerValor(movimiento);
        } else if (movimiento.contains("DEPOSITO")) {
            tipoMovimiento = "DEPOSITO";
            valor = extraerValor(movimiento);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no soportado: " + operacionDTO.getMovimiento());
        }

        MovimientoDTO dto = new MovimientoDTO();
        dto.setTipoMovimiento(tipoMovimiento);
        dto.setValor(valor != null ? java.math.BigDecimal.valueOf(valor) : null);
        dto.setSaldo(java.math.BigDecimal.valueOf(operacionDTO.getSaldoInicial()));
        dto.setCuentaId(cuenta.getId());
        dto.setDescripcion(operacionDTO.getMovimiento());
        // La fecha se asigna automáticamente en createMovimiento
        return dto;
    }

    private Double extraerValor(String movimiento) {
        // Extrae el primer número que encuentre en el string
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+([.,]\\d+)?)").matcher(movimiento);
        if (matcher.find()) {
            return Double.valueOf(matcher.group(1).replace(",", "."));
        }
        return null;
    }
} 