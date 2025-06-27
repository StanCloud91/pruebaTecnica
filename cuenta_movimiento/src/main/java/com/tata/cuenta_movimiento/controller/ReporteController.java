package com.tata.cuenta_movimiento.controller;

import com.tata.cuenta_movimiento.dto.ApiResponse;
import com.tata.cuenta_movimiento.dto.ReporteMovimientoDTO;
import com.tata.cuenta_movimiento.service.MovimientoService;
import com.tata.cuenta_movimiento.kafka.ClienteKafkaConsumer;
import com.tata.cuenta_movimiento.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final MovimientoService movimientoService;
    private final ClienteKafkaConsumer clienteKafkaConsumer;

    /**
     * Reporte de movimientos por rango de fechas y clienteId.
     */
    // @GetMapping
    // public ResponseEntity<ApiResponse<List<ReporteMovimientoDTO>>> getReporteMovimientos(
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
    //         @RequestParam Integer clienteId) {
    //     List<ReporteMovimientoDTO> reporte = movimientoService.obtenerReporteMovimientos(fechaInicio, fechaFin, clienteId);
    //     return ResponseEntity.ok(ApiResponse.success(reporte, "Reporte generado exitosamente"));
    // }

    @GetMapping
    public ApiResponse<List<ReporteMovimientoDTO>> generarReporte(
            @RequestParam String identificacion,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {
        
        // Buscar el id del cliente en Redis usando la identificación
        Integer clienteId = clienteKafkaConsumer.obtenerIdClientePorIdentificacion(identificacion);
        if (clienteId == null) {
            throw new ResourceNotFoundException("Cliente con identificación " + identificacion + " no encontrado en Redis");
        }
        
        List<ReporteMovimientoDTO> reporte = movimientoService.obtenerReporteMovimientos(fechaInicio, fechaFin, clienteId);
        return ApiResponse.success(reporte, "Reporte generado exitosamente");
    }
} 