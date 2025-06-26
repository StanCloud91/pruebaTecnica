package com.tata.cuenta_movimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Clase de respuesta estándar para todas las APIs del microservicio.
 * 
 * <p>Esta clase proporciona una estructura consistente para todas las respuestas
 * de la API, incluyendo información sobre el éxito de la operación, mensajes
 * descriptivos y datos de respuesta.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    
    /**
     * Crea una respuesta exitosa con datos y mensaje personalizado.
     * 
     * @param data Datos de la respuesta
     * @param message Mensaje descriptivo
     * @return ApiResponse con éxito
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }
    
    /**
     * Crea una respuesta exitosa con datos y mensaje por defecto.
     * 
     * @param data Datos de la respuesta
     * @return ApiResponse con éxito
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operación exitosa", data, LocalDateTime.now(), null);
    }
    
    /**
     * Crea una respuesta de error con mensaje.
     * 
     * @param message Mensaje de error
     * @return ApiResponse con error
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }
    
    /**
     * Crea una respuesta de error con mensaje y ruta.
     * 
     * @param message Mensaje de error
     * @param path Ruta de la petición
     * @return ApiResponse con error
     */
    public static <T> ApiResponse<T> error(String message, String path) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), path);
    }
} 