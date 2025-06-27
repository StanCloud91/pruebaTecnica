package com.tata.cuenta_movimiento.controller;

import com.tata.cuenta_movimiento.dto.ApiResponse;
import com.tata.cuenta_movimiento.dto.MovimientoDTO;
import com.tata.cuenta_movimiento.dto.MovimientoOperacionDTO;
import com.tata.cuenta_movimiento.dto.ReporteMovimientoDTO;
import com.tata.cuenta_movimiento.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de movimientos bancarios.
 * 
 * <p>Este controlador proporciona endpoints para realizar operaciones CRUD
 * completas sobre la entidad Movimiento, incluyendo búsquedas por diferentes criterios
 * y gestión de transacciones con actualización automática de saldos.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovimientoController {
    
    private final MovimientoService movimientoService;
    
    /**
     * Obtiene todos los movimientos registrados en el sistema.
     * 
     * @return ResponseEntity con la lista de movimientos y mensaje de éxito
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoDTO>>> getAllMovimientos() {
        List<MovimientoDTO> movimientos = movimientoService.getAllMovimientos();
        return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos obtenidos exitosamente"));
    }
    
    /**
     * Obtiene un movimiento específico por su ID.
     * 
     * @param id ID del movimiento
     * @return ResponseEntity con los datos del movimiento encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDTO>> getMovimientoById(@PathVariable Long id) {
        MovimientoDTO movimiento = movimientoService.getMovimientoById(id);
        return ResponseEntity.ok(ApiResponse.success(movimiento, "Movimiento obtenido exitosamente"));
    }
    
    
    /**
     * Crea un nuevo movimiento en el sistema y actualiza el saldo de la cuenta.
     * 
     * @param movimientoDTO Datos del movimiento a crear
     * @return ResponseEntity con el movimiento creado y código 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoDTO>> createMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO createdMovimiento = movimientoService.createMovimiento(movimientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdMovimiento, "Movimiento creado exitosamente"));
    }
    
    /**
     * Actualiza completamente los datos de un movimiento existente.
     * 
     * @param id ID del movimiento a actualizar
     * @param movimientoDTO Nuevos datos del movimiento
     * @return ResponseEntity con el movimiento actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDTO>> updateMovimiento(@PathVariable Long id, 
                                                                      @Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO updatedMovimiento = movimientoService.updateMovimiento(id, movimientoDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedMovimiento, "Movimiento actualizado exitosamente"));
    }
    
    /**
     * Elimina permanentemente un movimiento del sistema.
     * 
     * @param id ID del movimiento a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMovimiento(@PathVariable Long id) {
        movimientoService.deleteMovimiento(id);
        return ResponseEntity.ok(ApiResponse.success("Movimiento eliminado exitosamente"));
    }

    /**
     * Crea un nuevo movimiento a partir de la estructura especial de operación.
     * 
     * @param operacionDTO Datos de la operación
     * @return ResponseEntity con el movimiento creado y código 201
     */
    @PostMapping("/operacion")
    public ResponseEntity<ApiResponse<MovimientoDTO>> createMovimientoOperacion(@RequestBody MovimientoOperacionDTO operacionDTO) {
        MovimientoDTO movimientoDTO = movimientoService.convertirOperacionAMovimiento(operacionDTO);
        MovimientoDTO createdMovimiento = movimientoService.createMovimiento(movimientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdMovimiento, "Movimiento creado exitosamente"));
    }

    /**
     * Reporte de movimientos por rango de fechas y clienteId.
     */
    @GetMapping("/reportes")
    public ResponseEntity<ApiResponse<java.util.List<ReporteMovimientoDTO>>> getReporteMovimientos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin,
            @RequestParam Integer clienteId) {
        java.util.List<ReporteMovimientoDTO> reporte = movimientoService.obtenerReporteMovimientos(fechaInicio, fechaFin, clienteId);
        return ResponseEntity.ok(ApiResponse.success(reporte, "Reporte generado exitosamente"));
    }
} 