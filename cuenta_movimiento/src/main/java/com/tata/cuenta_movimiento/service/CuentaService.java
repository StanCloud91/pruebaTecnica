package com.tata.cuenta_movimiento.service;

import com.tata.cuenta_movimiento.dto.CuentaDTO;
import com.tata.cuenta_movimiento.entity.Cuenta;
import com.tata.cuenta_movimiento.exception.DuplicateResourceException;
import com.tata.cuenta_movimiento.exception.ResourceNotFoundException;
import com.tata.cuenta_movimiento.kafka.ClienteKafkaConsumer;
import com.tata.cuenta_movimiento.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de cuentas bancarias.
 * 
 * <p>Esta clase contiene toda la lógica de negocio relacionada con la gestión
 * de cuentas, incluyendo operaciones CRUD, validaciones de negocio y
 * transformaciones entre entidades y DTOs.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Service
@RequiredArgsConstructor
public class CuentaService {
    
    private final CuentaRepository cuentaRepository;
    @Autowired
    private ClienteKafkaConsumer clienteKafkaConsumer;
    
    /**
     * Obtiene todas las cuentas registradas en el sistema.
     * 
     * @return Lista de todas las cuentas convertidas a DTOs
     */
    public List<CuentaDTO> getAllCuentas() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        return cuentas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una cuenta específica por su ID.
     * 
     * @param id ID de la cuenta
     * @return DTO de la cuenta encontrada
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    public CuentaDTO getCuentaById(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        return convertToDTO(cuenta);
    }
    
    /**
     * Obtiene una cuenta específica por su número de cuenta.
     * 
     * @param numeroCuenta Número de cuenta
     * @return DTO de la cuenta encontrada
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    public CuentaDTO getCuentaByNumeroCuenta(Integer numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "número de cuenta", numeroCuenta));
        return convertToDTO(cuenta);
    }
    
    /**
     * Obtiene todas las cuentas de un cliente específico.
     * 
     * @param clienteId ID del cliente
     * @return Lista de cuentas del cliente
     */
    public List<CuentaDTO> getCuentasByClienteId(Integer clienteId) {
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        return cuentas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todas las cuentas de un tipo específico.
     * 
     * @param tipoCuenta Tipo de cuenta (AHORROS, CORRIENTE, PLAZO_FIJO)
     * @return Lista de cuentas del tipo especificado
     */
    public List<CuentaDTO> getCuentasByTipo(String tipoCuenta) {
        List<Cuenta> cuentas = cuentaRepository.findByTipoCuenta(tipoCuenta);
        return cuentas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva cuenta en el sistema.
     * 
     * @param cuentaDTO Datos de la cuenta a crear
     * @return DTO de la cuenta creada con ID asignado
     * @throws DuplicateResourceException si el número de cuenta ya existe
     */
    public CuentaDTO createCuenta(CuentaDTO cuentaDTO) {
        // Buscar cliente por nombre en Redis
        Integer clienteId = clienteKafkaConsumer.obtenerIdClientePorNombre(cuentaDTO.getCliente());
        if (clienteId == null) {
            throw new ResourceNotFoundException("No existe el Cliente");
        }
        
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new DuplicateResourceException("Cuenta", "número de cuenta", cuentaDTO.getNumeroCuenta());
        }
        
        //Cuenta cuenta = convertToEntity(cuentaDTO);
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldo(cuentaDTO.getSaldo());
        cuenta.setClienteId(clienteId);
        cuenta.setEstado(cuentaDTO.getEstado());
        // Asignar el clienteId obtenido de Redis
        Cuenta savedCuenta = cuentaRepository.save(cuenta);
        return convertToDTO(savedCuenta);
    }
    
    /**
     * Actualiza completamente los datos de una cuenta existente.
     * 
     * @param id ID de la cuenta a actualizar
     * @param cuentaDTO Nuevos datos de la cuenta
     * @return DTO de la cuenta actualizada
     * @throws ResourceNotFoundException si la cuenta no existe
     * @throws DuplicateResourceException si el número de cuenta ya existe en otra cuenta
     */
    public CuentaDTO updateCuenta(Long id, CuentaDTO cuentaDTO) {
        Cuenta existingCuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        
        if (cuentaRepository.existsByNumeroCuentaAndIdNot(cuentaDTO.getNumeroCuenta(), id)) {
            throw new DuplicateResourceException("Cuenta", "número de cuenta", cuentaDTO.getNumeroCuenta());
        }
        
        // Buscar cliente por nombre en Redis
        Integer clienteId = clienteKafkaConsumer.obtenerIdClientePorNombre(cuentaDTO.getCliente());
        if (clienteId == null) {
            throw new ResourceNotFoundException("No existe el Cliente");
        }
        
        // Actualizar campos
        existingCuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        existingCuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        existingCuenta.setSaldo(cuentaDTO.getSaldo());
        existingCuenta.setClienteId(clienteId);
        existingCuenta.setEstado(cuentaDTO.getEstado());
        
        Cuenta updatedCuenta = cuentaRepository.save(existingCuenta);
        return convertToDTO(updatedCuenta);
    }
    
    /**
     * Actualiza el saldo de una cuenta.
     * 
     * @param id ID de la cuenta
     * @param nuevoSaldo Nuevo saldo de la cuenta
     * @return DTO de la cuenta actualizada
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    public CuentaDTO updateSaldo(Long id, java.math.BigDecimal nuevoSaldo) {
        Cuenta existingCuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
        
        existingCuenta.setSaldo(nuevoSaldo);
        Cuenta updatedCuenta = cuentaRepository.save(existingCuenta);
        return convertToDTO(updatedCuenta);
    }
    
    /**
     * Elimina permanentemente una cuenta del sistema.
     * 
     * @param id ID de la cuenta a eliminar
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    public void deleteCuenta(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cuenta", "id", id);
        }
        cuentaRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Cuenta a su correspondiente DTO.
     * 
     * @param cuenta Entidad Cuenta a convertir
     * @return DTO correspondiente con los datos de la entidad
     */
    private CuentaDTO convertToDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        BeanUtils.copyProperties(cuenta, dto);
        // Obtener el nombre del cliente por ID y asignarlo al campo cliente
        String nombreCliente = clienteKafkaConsumer.obtenerNombreCliente(cuenta.getClienteId());
        dto.setCliente(nombreCliente != null ? nombreCliente : "Cliente no encontrado");
        return dto;
    }
    
    /**
     * Convierte un DTO Cuenta a su correspondiente entidad.
     * 
     * @param dto DTO Cuenta a convertir
     * @return Entidad correspondiente con los datos del DTO
     */
    private Cuenta convertToEntity(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        BeanUtils.copyProperties(dto, cuenta);
        // No copiar el campo cliente ya que se maneja por separado
        cuenta.setClienteId(null); // Se asignará después
        return cuenta;
    }
} 