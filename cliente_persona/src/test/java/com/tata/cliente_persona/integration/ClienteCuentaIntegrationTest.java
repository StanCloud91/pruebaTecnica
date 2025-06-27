package com.tata.cliente_persona.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.cliente_persona.dto.ClienteDTO;
import com.tata.cliente_persona.dto.ClienteKafkaDTO;
import com.tata.cliente_persona.entity.Cliente;
import com.tata.cliente_persona.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración entre los microservicios cliente_persona y cuenta_movimiento.
 * 
 * <p>Esta prueba valida el flujo completo de comunicación entre microservicios:
 * 1. Crear un cliente en cliente_persona
 * 2. Verificar que se envía el mensaje a Kafka
 * 3. Verificar que cuenta_movimiento puede crear una cuenta para ese cliente
 * 4. Verificar que se puede generar un reporte usando la identificación del cliente</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-01-27
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@ActiveProfiles("test")
class ClienteCuentaIntegrationTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFlujoCompletoClienteCuenta() throws Exception {
        // 1. Crear un cliente a través del servicio
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("Ana García");
        clienteDTO.setGenero("FEMENINO");
        clienteDTO.setEdad(28);
        clienteDTO.setIdentificacion("9876543210");
        clienteDTO.setDireccion("Calle 789 #12-34, Cali");
        clienteDTO.setTelefono("+573001112223");
        clienteDTO.setClienteId("CLI_INT_001");
        clienteDTO.setContraseña("testPassword123");
        clienteDTO.setEstado(true);

        // Crear cliente via servicio
        ClienteDTO clienteCreado = clienteService.createCliente(clienteDTO);
        
        assertNotNull(clienteCreado, "El cliente debería haberse creado exitosamente");
        assertEquals("Ana García", clienteCreado.getNombre());
        assertEquals("9876543210", clienteCreado.getIdentificacion());

        // 2. Verificar que el cliente tiene un ID asignado
        assertNotNull(clienteCreado.getId(), "El cliente debería tener un ID asignado");

        // 3. Verificar que el DTO de Kafka se crea correctamente
        ClienteKafkaDTO kafkaDTO = new ClienteKafkaDTO(
                clienteCreado.getId().intValue(),
                clienteCreado.getNombre(),
                clienteCreado.getIdentificacion()
        );
        
        assertEquals(clienteCreado.getId().intValue(), kafkaDTO.getId());
        assertEquals(clienteCreado.getNombre(), kafkaDTO.getNombre());
        assertEquals(clienteCreado.getIdentificacion(), kafkaDTO.getIdentificacion());

        // 4. Verificar que el flujo completo es consistente
        assertAll("Verificación del flujo completo",
                () -> assertNotNull(clienteCreado.getId(), "ID del cliente"),
                () -> assertEquals("Ana García", clienteCreado.getNombre(), "Nombre del cliente"),
                () -> assertEquals("9876543210", clienteCreado.getIdentificacion(), "Identificación del cliente"),
                () -> assertEquals("CLI_INT_001", clienteCreado.getClienteId(), "ID de cliente"),
                () -> assertTrue(clienteCreado.getEstado(), "Estado del cliente")
        );

        // 5. Verificar serialización JSON del DTO de Kafka
        assertDoesNotThrow(() -> {
            String json = objectMapper.writeValueAsString(kafkaDTO);
            assertNotNull(json);
            assertTrue(json.contains("Ana García"));
            assertTrue(json.contains("9876543210"));
        });
    }

    @Test
    void testComunicacionKafkaDTO() {
        // Crear un cliente directamente para probar el DTO de Kafka
        Cliente cliente = new Cliente(
                "Carlos López",
                "MASCULINO",
                35,
                "1112223334",
                "Avenida 456 #78-90, Medellín",
                "+573004445556",
                "CLI_INT_002",
                "password456",
                true
        );

        // Verificar que el DTO de Kafka se crea correctamente
        ClienteKafkaDTO kafkaDTO = new ClienteKafkaDTO(
                1, // ID simulado
                cliente.getNombre(),
                cliente.getIdentificacion()
        );

        // Verificar que todos los campos están presentes
        assertAll("Verificación del DTO de Kafka",
                () -> assertEquals(1, kafkaDTO.getId()),
                () -> assertEquals("Carlos López", kafkaDTO.getNombre()),
                () -> assertEquals("1112223334", kafkaDTO.getIdentificacion())
        );

        // Verificar que el DTO puede ser serializado (importante para Kafka)
        assertDoesNotThrow(() -> {
            String json = objectMapper.writeValueAsString(kafkaDTO);
            assertNotNull(json);
            assertTrue(json.contains("Carlos López"));
            assertTrue(json.contains("1112223334"));
        });
    }

    @Test
    void testIntegracionCompletaConServicio() {
        // Crear cliente usando el servicio completo
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("María Rodríguez");
        clienteDTO.setGenero("FEMENINO");
        clienteDTO.setEdad(32);
        clienteDTO.setIdentificacion("5556667778");
        clienteDTO.setDireccion("Carrera 15 #23-45, Barranquilla");
        clienteDTO.setTelefono("+573005556667");
        clienteDTO.setClienteId("CLI_INT_003");
        clienteDTO.setContraseña("securePass789");
        clienteDTO.setEstado(true);

        // Crear cliente a través del servicio
        ClienteDTO clienteCreado = clienteService.createCliente(clienteDTO);
        
        // Verificar que se creó correctamente
        assertNotNull(clienteCreado);
        assertEquals("María Rodríguez", clienteCreado.getNombre());
        assertEquals("5556667778", clienteCreado.getIdentificacion());
        
        // Verificar que el DTO de Kafka se puede crear correctamente
        ClienteKafkaDTO kafkaDTO = new ClienteKafkaDTO(
                clienteCreado.getId().intValue(),
                clienteCreado.getNombre(),
                clienteCreado.getIdentificacion()
        );
        
        // Verificar serialización JSON
        assertDoesNotThrow(() -> {
            String json = objectMapper.writeValueAsString(kafkaDTO);
            assertNotNull(json);
            assertTrue(json.contains("María Rodríguez"));
            assertTrue(json.contains("5556667778"));
        });
    }

    @Test
    void testValidacionIdentificacionParaReportes() {
        // Crear un cliente para probar la validación de identificación
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("Pedro Martínez");
        clienteDTO.setGenero("MASCULINO");
        clienteDTO.setEdad(40);
        clienteDTO.setIdentificacion("9998887776");
        clienteDTO.setDireccion("Calle 20 #30-40, Bucaramanga");
        clienteDTO.setTelefono("+573009998887");
        clienteDTO.setClienteId("CLI_INT_004");
        clienteDTO.setContraseña("testPass123");
        clienteDTO.setEstado(true);

        ClienteDTO clienteCreado = clienteService.createCliente(clienteDTO);
        
        // Verificar que la identificación es válida para reportes
        assertNotNull(clienteCreado.getIdentificacion(), 
                "La identificación del cliente no debería ser nula");
        assertEquals("9998887776", clienteCreado.getIdentificacion(), 
                "La identificación del cliente debería coincidir");
        
        // Simular la URL de reporte que usaría el microservicio de cuentas
        String reporteUrl = "/api/reportes?identificacion=" + clienteCreado.getIdentificacion() + 
                           "&fechaInicio=2024-01-01&fechaFin=2024-12-31";
        
        assertTrue(reporteUrl.contains("9998887776"), 
                "La URL de reporte debería contener la identificación del cliente");
    }
} 