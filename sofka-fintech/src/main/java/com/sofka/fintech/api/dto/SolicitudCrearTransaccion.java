package com.sofka.fintech.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SolicitudCrearTransaccion {

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal monto;
    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

}
