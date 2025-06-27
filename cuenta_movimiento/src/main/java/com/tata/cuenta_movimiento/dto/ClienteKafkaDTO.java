package com.tata.cuenta_movimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteKafkaDTO {
    private Integer id;
    private String nombre;
    private String identificacion;
} 