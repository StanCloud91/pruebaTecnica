package com.tata.cuenta_movimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación CuentaMovimiento.
 * 
 * <p>Esta es la clase de entrada para el microservicio de gestión de cuentas
 * y movimientos bancarios. La aplicación proporciona endpoints REST para
 * realizar operaciones CRUD completas sobre cuentas y movimientos.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Gestión completa de cuentas bancarias</li>
 *   <li>Gestión de movimientos con actualización automática de saldos</li>
 *   <li>Validaciones de fondos para retiros</li>
 *   <li>Búsquedas por múltiples criterios</li>
 *   <li>Manejo global de excepciones</li>
 *   <li>Respuestas estandarizadas en formato JSON</li>
 * </ul>
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@SpringBootApplication
public class CuentaMovimientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuentaMovimientoApplication.class, args);
	}

}
