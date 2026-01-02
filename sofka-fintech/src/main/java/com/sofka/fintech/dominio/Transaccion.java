package com.sofka.fintech.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("TRANSACCIONES")
public class Transaccion {

    @Id
    @Column("ID_TRANSACCION")
    private Long idTransaccion;

    @Column("MONTO")
    private BigDecimal monto;

    @Column("COMISION")
    private BigDecimal comision;

    @Column("FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    public Transaccion() {
    }

    public Transaccion(Long idTransaccion, BigDecimal monto, BigDecimal comision, LocalDateTime fechaRegistro) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
        this.comision = comision;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Long idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
