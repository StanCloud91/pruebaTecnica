package com.tata.cliente_persona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar datos de Cliente por Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteKafkaDTO {
    private Integer id;
    private String nombre;
} 