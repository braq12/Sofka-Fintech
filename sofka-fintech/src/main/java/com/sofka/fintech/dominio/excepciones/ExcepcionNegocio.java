package com.sofka.fintech.dominio.excepciones;

import lombok.Getter;

@Getter
public class ExcepcionNegocio extends RuntimeException {

    private final String codigo;

    public ExcepcionNegocio(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

}
