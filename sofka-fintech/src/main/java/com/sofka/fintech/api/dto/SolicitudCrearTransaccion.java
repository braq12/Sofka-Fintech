package com.sofka.fintech.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SolicitudCrearTransaccion {

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal monto;

    private String descripcion;

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
