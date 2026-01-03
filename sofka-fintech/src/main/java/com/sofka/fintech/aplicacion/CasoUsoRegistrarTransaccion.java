package com.sofka.fintech.aplicacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.api.dto.SolicitudCrearTransaccion;
import reactor.core.publisher.Mono;

public interface CasoUsoRegistrarTransaccion {
    Mono<RespuestaTransaccion> ejecutar(SolicitudCrearTransaccion solicitud);
}
