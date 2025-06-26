package com.tata.cliente_persona.service;

import com.tata.cliente_persona.dto.ClienteDTO;
import com.tata.cliente_persona.dto.ClienteKafkaDTO;
import com.tata.cliente_persona.entity.Cliente;
import com.tata.cliente_persona.exception.DuplicateResourceException;
import com.tata.cliente_persona.exception.ResourceNotFoundException;
import com.tata.cliente_persona.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de clientes.
 * 
 * <p>Esta clase contiene toda la lógica de negocio relacionada con la gestión
 * de clientes, incluyendo operaciones CRUD, validaciones de negocio y
 * transformaciones entre entidades y DTOs.</p>
 * 
 * <p>El servicio maneja las siguientes operaciones principales:</p>
 * <ul>
 *   <li>Creación de nuevos clientes con validaciones de unicidad</li>
 *   <li>Búsqueda de clientes por ID</li>
 *   <li>Actualización de datos de clientes existentes</li>
 *   <li>Eliminación de clientes</li>
 * </ul>
 * 
 * <p>El servicio utiliza el patrón de inyección de dependencias para acceder
 * al repositorio de datos y maneja las excepciones de negocio apropiadas.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 * @see ClienteRepository
 * @see ClienteDTO
 * @see Cliente
 */
@Service
@RequiredArgsConstructor
public class ClienteService {
    
    /**
     * Repositorio para operaciones de persistencia de clientes.
     */
    private final ClienteRepository clienteRepository;
    
    @Autowired
    private ClienteKafkaProducer clienteKafkaProducer;
    
    /**
     * Obtiene todos los clientes registrados en el sistema.
     * 
     * <p>Este método recupera todos los clientes de la base de datos,
     * independientemente de su estado, y los convierte a DTOs para
     * la capa de presentación.</p>
     * 
     * @return Lista de todos los clientes convertidos a DTOs
     * @apiNote Este método no requiere parámetros y retorna todos los registros
     * @example
     * <pre>
     * List&lt;ClienteDTO&gt; clientes = clienteService.getAllClientes();
     * // Retorna: [ClienteDTO{id=1, nombre="Juan", ...}, ClienteDTO{id=2, ...}]
     * </pre>
     */
    public List<ClienteDTO> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un cliente específico por su ID interno.
     * 
     * <p>Este método busca un cliente utilizando su clave primaria (ID interno)
     * generado automáticamente por la base de datos.</p>
     * 
     * @param id ID interno del cliente (clave primaria)
     * @return DTO del cliente encontrado
     * @throws ResourceNotFoundException si no existe un cliente con el ID especificado
     * @apiNote El ID debe ser un valor válido mayor a 0
     * @example
     * <pre>
     * ClienteDTO cliente = clienteService.getClienteById(1L);
     * // Retorna: ClienteDTO{id=1, nombre="Juan", clienteId="CLI001", ...}
     * </pre>
     */
    public ClienteDTO getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertToDTO(cliente);
    }
    
    
    /**
     * Crea un nuevo cliente en el sistema.
     * 
     * <p>Este método valida que no existan duplicados en clienteId e identificación,
     * convierte el DTO a entidad, persiste los datos y retorna el cliente creado.</p>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Verifica que el clienteId no exista en otro cliente</li>
     *   <li>Verifica que la identificación no exista en otro cliente</li>
     *   <li>Las validaciones de campos se realizan a nivel de DTO</li>
     * </ul>
     * 
     * @param clienteDTO Datos del cliente a crear
     * @return DTO del cliente creado con ID asignado
     * @throws DuplicateResourceException si el clienteId o identificación ya existen
     * @apiNote El clienteDTO debe contener todos los campos obligatorios válidos
     * @example
     * <pre>
     * ClienteDTO nuevoCliente = new ClienteDTO("Juan", "MASCULINO", 30, ...);
     * ClienteDTO creado = clienteService.createCliente(nuevoCliente);
     * // Retorna: ClienteDTO{id=1, nombre="Juan", clienteId="CLI001", ...}
     * </pre>
     */
    public ClienteDTO createCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new DuplicateResourceException("Cliente", "identificación", clienteDTO.getIdentificacion());
        }
        
        if (clienteRepository.existsByClienteId(clienteDTO.getClienteId())) {
            throw new DuplicateResourceException("Cliente", "clienteId", clienteDTO.getClienteId());
        }
        
        Cliente cliente = convertToEntity(clienteDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        // Enviar mensaje a Kafka
        ClienteKafkaDTO kafkaDTO = new ClienteKafkaDTO(savedCliente.getId().intValue(), savedCliente.getNombre());
        clienteKafkaProducer.enviarCliente(kafkaDTO);
        return convertToDTO(savedCliente);
    }
    
    /**
     * Actualiza completamente los datos de un cliente existente.
     * 
     * <p>Este método permite modificar todos los campos de un cliente,
     * incluyendo datos personales y específicos de cliente. Valida que
     * no se dupliquen clienteId e identificación con otros clientes.</p>
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Verifica que el cliente exista</li>
     *   <li>Verifica que el clienteId no exista en otro cliente (excluyendo el actual)</li>
     *   <li>Verifica que la identificación no exista en otro cliente (excluyendo el actual)</li>
     * </ul>
     * 
     * @param id ID interno del cliente a actualizar
     * @param clienteDTO Nuevos datos del cliente
     * @return DTO del cliente actualizado
     * @throws ResourceNotFoundException si el cliente no existe
     * @throws DuplicateResourceException si el clienteId o identificación ya existen en otro cliente
     * @apiNote El clienteDTO debe contener todos los campos obligatorios válidos
     * @example
     * <pre>
     * ClienteDTO datosActualizados = new ClienteDTO("Juan Carlos", "MASCULINO", 31, ...);
     * ClienteDTO actualizado = clienteService.updateCliente(1L, datosActualizados);
     * </pre>
     */
    public ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        if (clienteRepository.existsByIdentificacionAndIdNot(clienteDTO.getIdentificacion(), id)) {
            throw new DuplicateResourceException("Cliente", "identificación", clienteDTO.getIdentificacion());
        }
        
        if (clienteRepository.existsByClienteIdAndIdNot(clienteDTO.getClienteId(), id)) {
            throw new DuplicateResourceException("Cliente", "clienteId", clienteDTO.getClienteId());
        }
        
        // Actualizar campos de persona
        existingCliente.setNombre(clienteDTO.getNombre());
        existingCliente.setGenero(clienteDTO.getGenero());
        existingCliente.setEdad(clienteDTO.getEdad());
        existingCliente.setIdentificacion(clienteDTO.getIdentificacion());
        existingCliente.setDireccion(clienteDTO.getDireccion());
        existingCliente.setTelefono(clienteDTO.getTelefono());
        
        // Actualizar campos específicos de cliente
        existingCliente.setClienteId(clienteDTO.getClienteId());
        existingCliente.setContraseña(clienteDTO.getContraseña());
        existingCliente.setEstado(clienteDTO.getEstado());
        
        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return convertToDTO(updatedCliente);
    }
    
    
    
    /**
     * Elimina permanentemente un cliente del sistema.
     * 
     * <p>Este método elimina completamente un cliente de la base de datos.
     * La operación es irreversible y elimina tanto los datos personales
     * como los específicos del cliente.</p>
     * 
     * @param id ID interno del cliente a eliminar
     * @throws ResourceNotFoundException si el cliente no existe
     * @apiNote Esta operación es irreversible y elimina todos los datos del cliente
     * @example
     * <pre>
     * clienteService.deleteCliente(1L);
     * // El cliente con ID 1 ha sido eliminado permanentemente
     * </pre>
     */
    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }
        clienteRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Cliente a su correspondiente DTO.
     * 
     * <p>Este método privado utiliza BeanUtils para copiar las propiedades
     * de la entidad al DTO, facilitando la separación entre la capa de
     * persistencia y la capa de presentación.</p>
     * 
     * @param cliente Entidad Cliente a convertir
     * @return DTO correspondiente con los datos de la entidad
     * @apiNote Método privado utilizado internamente para conversiones
     */
    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        BeanUtils.copyProperties(cliente, dto);
        return dto;
    }
    
    /**
     * Convierte un DTO Cliente a su correspondiente entidad.
     * 
     * <p>Este método privado utiliza BeanUtils para copiar las propiedades
     * del DTO a la entidad, facilitando la separación entre la capa de
     * presentación y la capa de persistencia.</p>
     * 
     * @param dto DTO Cliente a convertir
     * @return Entidad correspondiente con los datos del DTO
     * @apiNote Método privado utilizado internamente para conversiones
     */
    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        BeanUtils.copyProperties(dto, cliente);
        return cliente;
    }
} 