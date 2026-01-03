package com.sofka.fintech.dominio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
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

    @Column("DESCRIPCION")
    private String descripcion;

    public Transaccion() {
    }

    public Transaccion(Long idTransaccion, BigDecimal monto, BigDecimal comision, LocalDateTime fechaRegistro, String descripcion) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
        this.comision = comision;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;
    }

}
