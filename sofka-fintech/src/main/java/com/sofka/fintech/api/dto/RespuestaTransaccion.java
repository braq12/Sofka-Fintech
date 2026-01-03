package com.sofka.fintech.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RespuestaTransaccion {

    private Long idTransaccion;
    private BigDecimal monto;
    private BigDecimal comision;
    private LocalDateTime fechaRegistro;
    private String descripcion;

    public RespuestaTransaccion(Long idTransaccion, BigDecimal monto, BigDecimal comision, LocalDateTime fechaRegistro, String descripcion) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
        this.comision = comision;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getIdTransaccion() {
        return idTransaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
