package com.sofka.fintech.puertos.salida;

import com.sofka.fintech.dominio.Transaccion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RepositorioTransaccion {
    Mono<Transaccion> guardar(Transaccion transaccion);
    Flux<Transaccion> listar();
}
