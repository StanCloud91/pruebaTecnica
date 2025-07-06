package com.tata.cuenta_movimiento.dto;

public interface ReporteMovimientoView {
    String getFecha();
    String getCliente();
    String getNumeroCuenta();
    String getTipo();
    String getSaldoInicial();
    String getEstado();
    String getMovimiento();
    String getSaldoDisponible();

}
