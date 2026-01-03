package com.sofka.fintech.dominio.excepciones;

public class ExcepcionNegocio extends RuntimeException {

    private final String codigo;

    public ExcepcionNegocio(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
