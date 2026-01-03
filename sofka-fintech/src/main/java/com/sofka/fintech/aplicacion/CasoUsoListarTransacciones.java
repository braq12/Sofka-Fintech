package com.sofka.fintech.aplicacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import reactor.core.publisher.Flux;

public interface CasoUsoListarTransacciones {
    Flux<RespuestaTransaccion> ejecutar();
}
