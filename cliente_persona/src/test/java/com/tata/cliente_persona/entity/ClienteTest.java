package com.tata.cliente_persona.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

/**
 * Pruebas unitarias para la entidad Cliente.
 * 
 * <p>Esta clase contiene pruebas que validan el comportamiento de la entidad Cliente,
 * incluyendo su herencia de Persona, validaciones de campos y métodos de negocio.</p>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-01-27
 */
class ClienteTest {

    private Validator validator;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Crear un cliente válido para las pruebas
        cliente = new Cliente(
            "Juan Pérez",           // nombre
            "MASCULINO",           // genero
            30,                    // edad
            "1234567890",          // identificacion
            "Calle 123 #45-67, Bogotá", // direccion
            "+573001234567",       // telefono
            "CLI001",              // clienteId
            "password123",         // contraseña
            true                   // estado
        );
    }

    /**
     * Prueba que un cliente válido pase todas las validaciones.
     */
    @Test
    void testClienteValido() {
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertTrue(violations.isEmpty(), 
            "Un cliente válido no debería tener violaciones de validación. Violaciones encontradas: " + violations);
    }

    /**
     * Prueba que la herencia de Persona funcione correctamente.
     */
    @Test
    void testHerenciaDePersona() {
        assertEquals("Juan Pérez", cliente.getNombre());
        assertEquals("MASCULINO", cliente.getGenero());
        assertEquals(30, cliente.getEdad());
        assertEquals("1234567890", cliente.getIdentificacion());
        assertEquals("Calle 123 #45-67, Bogotá", cliente.getDireccion());
        assertEquals("+573001234567", cliente.getTelefono());
    }

    /**
     * Prueba que los campos específicos de Cliente se establezcan correctamente.
     */
    @Test
    void testCamposEspecificosDeCliente() {
        assertEquals("CLI001", cliente.getClienteId());
        assertEquals("password123", cliente.getContraseña());
        assertTrue(cliente.getEstado());
    }

    /**
     * Prueba validación de clienteId vacío.
     */
    @Test
    void testClienteIdVacio() {
        cliente.setClienteId("");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("clienteId") && 
                          v.getMessage().contains("obligatorio")));
    }

    /**
     * Prueba validación de clienteId muy corto.
     */
    @Test
    void testClienteIdMuyCorto() {
        cliente.setClienteId("AB");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("clienteId") && 
                          v.getMessage().contains("entre 3 y 20 caracteres")));
    }

    /**
     * Prueba validación de contraseña vacía.
     */
    @Test
    void testContraseñaVacia() {
        cliente.setContraseña("");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("contraseña") && 
                          v.getMessage().contains("obligatoria")));
    }

    /**
     * Prueba validación de contraseña muy corta.
     */
    @Test
    void testContraseñaMuyCorta() {
        cliente.setContraseña("123");
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("contraseña") && 
                          v.getMessage().contains("entre 4 y 100 caracteres")));
    }

    /**
     * Prueba validación de estado nulo.
     */
    @Test
    void testEstadoNulo() {
        cliente.setEstado(null);
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("estado") && 
                          v.getMessage().contains("obligatorio")));
    }

    /**
     * Prueba que el constructor con parámetros funcione correctamente.
     */
    @Test
    void testConstructorConParametros() {
        Cliente nuevoCliente = new Cliente(
            "María García",
            "FEMENINO",
            25,
            "0987654321",
            "Avenida 456 #78-90, Medellín",
            "+573009876543",
            "CLI002",
            "securePass456",
            false
        );
        
        assertEquals("María García", nuevoCliente.getNombre());
        assertEquals("FEMENINO", nuevoCliente.getGenero());
        assertEquals(25, nuevoCliente.getEdad());
        assertEquals("0987654321", nuevoCliente.getIdentificacion());
        assertEquals("Avenida 456 #78-90, Medellín", nuevoCliente.getDireccion());
        assertEquals("+573009876543", nuevoCliente.getTelefono());
        assertEquals("CLI002", nuevoCliente.getClienteId());
        assertEquals("securePass456", nuevoCliente.getContraseña());
        assertFalse(nuevoCliente.getEstado());
    }

    /**
     * Prueba que los métodos equals y hashCode funcionen correctamente.
     */
    @Test
    void testEqualsYHashCode() {
        Cliente cliente1 = new Cliente(
            "Juan Pérez", "MASCULINO", 30, "1234567890",
            "Calle 123 #45-67, Bogotá", "+573001234567",
            "CLI001", "password123", true
        );
        
        Cliente cliente2 = new Cliente(
            "Juan Pérez", "MASCULINO", 30, "1234567890",
            "Calle 123 #45-67, Bogotá", "+573001234567",
            "CLI001", "password123", true
        );
        
        // Los clientes deberían ser iguales si tienen los mismos datos
        assertEquals(cliente1, cliente2);
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
    }

    /**
     * Prueba que el método toString incluya información relevante.
     */
    @Test
    void testToString() {
        String toString = cliente.toString();
        
        // Verificar que el toString no sea nulo y contenga información del cliente
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        
        // Verificar que contenga información básica del cliente
        assertTrue(toString.contains("CLI001") || toString.contains("clienteId=CLI001"));
    }
} 