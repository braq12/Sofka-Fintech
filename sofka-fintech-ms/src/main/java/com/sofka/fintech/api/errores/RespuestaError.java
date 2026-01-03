package com.sofka.fintech.api.errores;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class RespuestaError {

    private String codigo;
    private String mensaje;
    private List<String> detalles;
    private OffsetDateTime fecha;

    public RespuestaError(String codigo, String mensaje, List<String> detalles, OffsetDateTime fecha) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.detalles = detalles;
        this.fecha = fecha;
    }

}
