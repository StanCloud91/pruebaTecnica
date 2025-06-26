package com.tata.cuenta_movimiento.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.cuenta_movimiento.dto.ClienteKafkaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClienteKafkaConsumer {
    
    private static final String CLIENTES_KEY_PREFIX = "cliente:";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "clientes-topic", groupId = "grupo-cuentas")
    public void escucharCliente(String mensaje) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClienteKafkaDTO cliente = mapper.readValue(mensaje, ClienteKafkaDTO.class);
            // Almacenar en Redis con clave "cliente:{id}"
            String key = CLIENTES_KEY_PREFIX + cliente.getId();
            redisTemplate.opsForValue().set(key, cliente);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public boolean existeCliente(Integer id) {
        String key = CLIENTES_KEY_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public ClienteKafkaDTO obtenerCliente(Integer id) {
        String key = CLIENTES_KEY_PREFIX + id;
        return (ClienteKafkaDTO) redisTemplate.opsForValue().get(key);
    }

    public String obtenerNombreCliente(Integer id) {
        ClienteKafkaDTO cliente = obtenerCliente(id);
        return cliente != null ? cliente.getNombre() : null;
    }
} 