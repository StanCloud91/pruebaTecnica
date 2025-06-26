package com.tata.cuenta_movimiento.controller;

import com.tata.cuenta_movimiento.dto.ApiResponse;
import com.tata.cuenta_movimiento.dto.CuentaDTO;
import com.tata.cuenta_movimiento.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de cuentas bancarias.
 * 
 * <p>Este controlador proporciona endpoints para realizar operaciones CRUD
 * completas sobre la entidad Cuenta, incluyendo búsquedas por diferentes criterios
 * y gestión de saldos.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CuentaController {
    
    private final CuentaService cuentaService;
    
    /**
     * Obtiene todas las cuentas registradas en el sistema.
     * 
     * @return ResponseEntity con la lista de cuentas y mensaje de éxito
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> getAllCuentas() {
        List<CuentaDTO> cuentas = cuentaService.getAllCuentas();
        return ResponseEntity.ok(ApiResponse.success(cuentas, "Cuentas obtenidas exitosamente"));
    }
    
    /**
     * Obtiene una cuenta específica por su ID.
     * 
     * @param id ID de la cuenta
     * @return ResponseEntity con los datos de la cuenta encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> getCuentaById(@PathVariable Long id) {
        CuentaDTO cuenta = cuentaService.getCuentaById(id);
        return ResponseEntity.ok(ApiResponse.success(cuenta, "Cuenta obtenida exitosamente"));
    }
    
    
    /**
     * Crea una nueva cuenta en el sistema.
     * 
     * @param cuentaDTO Datos de la cuenta a crear
     * @return ResponseEntity con la cuenta creada y código 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDTO>> createCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO createdCuenta = cuentaService.createCuenta(cuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCuenta, "Cuenta creada exitosamente"));
    }
    
    /**
     * Actualiza completamente los datos de una cuenta existente.
     * 
     * @param id ID de la cuenta a actualizar
     * @param cuentaDTO Nuevos datos de la cuenta
     * @return ResponseEntity con la cuenta actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> updateCuenta(@PathVariable Long id, 
                                                              @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO updatedCuenta = cuentaService.updateCuenta(id, cuentaDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedCuenta, "Cuenta actualizada exitosamente"));
    }
    
    
    /**
     * Elimina permanentemente una cuenta del sistema.
     * 
     * @param id ID de la cuenta a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCuenta(@PathVariable Long id) {
        cuentaService.deleteCuenta(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta eliminada exitosamente"));
    }
} 