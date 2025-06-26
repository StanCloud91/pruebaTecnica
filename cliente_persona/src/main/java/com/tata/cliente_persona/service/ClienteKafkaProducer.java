package com.tata.cliente_persona.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.cliente_persona.dto.ClienteKafkaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClienteKafkaProducer {
    private static final String TOPIC = "clientes-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void enviarCliente(ClienteKafkaDTO cliente) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String mensaje = mapper.writeValueAsString(cliente);
            kafkaTemplate.send(TOPIC, mensaje);
        } catch (JsonProcessingException e) {
            // Manejo de error (puedes loguear o lanzar una excepción personalizada)
            e.printStackTrace();
        }
    }
} 