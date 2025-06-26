package com.tata.cuenta_movimiento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no hay fondos suficientes para realizar una transacción.
 * 
 * @author Stalin Salgado
 * @version 1.0
 * @since 2025-06-25
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String numeroCuenta, String saldoActual, String montoSolicitado) {
        super(String.format("Fondos insuficientes en la cuenta %s. Saldo actual: %s, Monto solicitado: %s", 
                           numeroCuenta, saldoActual, montoSolicitado));
    }
} 