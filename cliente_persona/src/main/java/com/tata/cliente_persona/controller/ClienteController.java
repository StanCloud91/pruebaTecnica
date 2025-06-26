package com.tata.cliente_persona.controller;

import com.tata.cliente_persona.dto.ApiResponse;
import com.tata.cliente_persona.dto.ClienteDTO;
import com.tata.cliente_persona.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de clientes.
 * 
 * <p>Este controlador proporciona endpoints para realizar operaciones CRUD
 * completas sobre la entidad Cliente, incluyendo búsquedas por diferentes criterios
 * y gestión de estados.</p>
 * 
 * <p>Los endpoints siguen las convenciones REST y devuelven respuestas
 * estandarizadas en formato JSON.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    /**
     * Obtiene todos los clientes registrados en el sistema.
     * 
     * <p>Este endpoint retorna una lista completa de todos los clientes,
     * independientemente de su estado (activo o inactivo).</p>
     * 
     * @return ResponseEntity con la lista de clientes y mensaje de éxito
     * @apiNote GET /api/clientes
     * @example
     * <pre>
     * GET /api/clientes
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Clientes obtenidos exitosamente",
     *   "data": [...],
     *   "timestamp": "2025-06-25T21:30:15.123"
     * }
     * </pre>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> getAllClientes() {
        List<ClienteDTO> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(ApiResponse.success(clientes, "Clientes obtenidos exitosamente"));
    }
    
    /**
     * Obtiene un cliente específico por su ID interno.
     * 
     * <p>Este endpoint busca un cliente utilizando su clave primaria (ID interno)
     * generado automáticamente por la base de datos.</p>
     * 
     * @param id ID interno del cliente (clave primaria)
     * @return ResponseEntity con los datos del cliente encontrado
     * @throws ResourceNotFoundException si el cliente no existe
     * @apiNote GET /api/clientes/{id}
     * @example
     * <pre>
     * GET /api/clientes/1
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Cliente obtenido exitosamente",
     *   "data": {
     *     "id": 1,
     *     "nombre": "Juan Pérez",
     *     "clienteId": "CLI001",
     *     ...
     *   }
     * }
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> getClienteById(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente obtenido exitosamente"));
    }
    
    
    /**
     * Crea un nuevo cliente en el sistema.
     * 
     * <p>Este endpoint permite registrar un nuevo cliente con todos sus datos
     * personales y específicos de cliente. Valida que no existan duplicados
     * en clienteId e identificación.</p>
     * 
     * @param clienteDTO Datos del cliente a crear
     * @return ResponseEntity con el cliente creado y código 201
     * @throws DuplicateResourceException si el clienteId o identificación ya existen
     * @throws MethodArgumentNotValidException si los datos no pasan validación
     * @apiNote POST /api/clientes
     * @example
     * <pre>
     * POST /api/clientes
     * {
     *   "nombre": "Juan Pérez",
     *   "genero": "MASCULINO",
     *   "edad": 30,
     *   "identificacion": "12345678",
     *   "direccion": "Calle 123",
     *   "telefono": "3001234567",
     *   "clienteId": "CLI001",
     *   "contraseña": "password123",
     *   "estado": true
     * }
     * </pre>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO createdCliente = clienteService.createCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCliente, "Cliente creado exitosamente"));
    }
    
    /**
     * Actualiza completamente los datos de un cliente existente.
     * 
     * <p>Este endpoint permite modificar todos los campos de un cliente,
     * incluyendo datos personales y específicos de cliente. Valida que
     * no se dupliquen clienteId e identificación con otros clientes.</p>
     * 
     * @param id ID interno del cliente a actualizar
     * @param clienteDTO Nuevos datos del cliente
     * @return ResponseEntity con el cliente actualizado
     * @throws ResourceNotFoundException si el cliente no existe
     * @throws DuplicateResourceException si el clienteId o identificación ya existen en otro cliente
     * @throws MethodArgumentNotValidException si los datos no pasan validación
     * @apiNote PUT /api/clientes/{id}
     * @example
     * <pre>
     * PUT /api/clientes/1
     * {
     *   "nombre": "Juan Carlos Pérez",
     *   "genero": "MASCULINO",
     *   "edad": 31,
     *   "identificacion": "12345678",
     *   "direccion": "Calle 456",
     *   "telefono": "3001234568",
     *   "clienteId": "CLI001",
     *   "contraseña": "newpassword123",
     *   "estado": false
     * }
     * </pre>
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> updateCliente(@PathVariable Long id, 
                                                                @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO updatedCliente = clienteService.updateCliente(id, clienteDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedCliente, "Cliente actualizado exitosamente"));
    }
    
    
    
    /**
     * Elimina permanentemente un cliente del sistema.
     * 
     * <p>Este endpoint elimina completamente un cliente de la base de datos.
     * La operación es irreversible y elimina tanto los datos personales
     * como los específicos del cliente.</p>
     * 
     * @param id ID interno del cliente a eliminar
     * @return ResponseEntity con mensaje de confirmación
     * @throws ResourceNotFoundException si el cliente no existe
     * @apiNote DELETE /api/clientes/{id}
     * @example
     * <pre>
     * DELETE /api/clientes/1
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Cliente eliminado exitosamente",
     *   "data": "Cliente eliminado exitosamente"
     * }
     * </pre>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente"));
    }
} 