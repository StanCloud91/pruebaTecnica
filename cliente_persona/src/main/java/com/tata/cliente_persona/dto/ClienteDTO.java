package com.tata.cliente_persona.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClienteDTO extends PersonaDTO {
    
    private Long id;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    private String contraseña;
    
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
    
    // Constructor con parámetros básicos
    public ClienteDTO(String nombre, String genero, Integer edad, String identificacion, 
                      String direccion, String telefono, String contraseña, Boolean estado) {
        super(nombre, genero, edad, identificacion, direccion, telefono);

        this.contraseña = contraseña;
        this.estado = estado;
    }
} 