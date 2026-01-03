package com.sofka.fintech.infraestructura.streaming;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.puertos.salida.PublicadorTransacciones;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class PublicadorTransaccionesSse implements PublicadorTransacciones {

    private final Sinks.Many<RespuestaTransaccion> canal;

    public PublicadorTransaccionesSse() {
        this.canal = Sinks.many().multicast().onBackpressureBuffer();
    }

    @Override
    public Mono<Void> publicar(RespuestaTransaccion transaccion) {
        Sinks.EmitResult resultado = canal.tryEmitNext(transaccion);
        if (resultado.isFailure()) {
            return Mono.empty();
        }
        return Mono.empty();
    }

    @Override
    public Flux<RespuestaTransaccion> flujo() {
        return canal.asFlux();
    }
}
