package com.sofka.fintech.puertos.salida;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PublicadorTransacciones {
    Mono<Void> publicar(RespuestaTransaccion transaccion);
    Flux<RespuestaTransaccion> flujo();
}

