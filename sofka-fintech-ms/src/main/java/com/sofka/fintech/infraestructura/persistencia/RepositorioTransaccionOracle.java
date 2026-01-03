package com.sofka.fintech.infraestructura.persistencia;

import com.sofka.fintech.dominio.Transaccion;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RepositorioTransaccionOracle implements RepositorioTransaccion {

    private final RepositorioTransaccionR2dbcSpring repositorioSpring;

    public RepositorioTransaccionOracle(RepositorioTransaccionR2dbcSpring repositorioSpring) {
        this.repositorioSpring = repositorioSpring;
    }

    @Override
    public Mono<Transaccion> guardar(Transaccion transaccion) {
        return repositorioSpring.save(transaccion);
    }

    @Override
    public Flux<Transaccion> listar() {
        return repositorioSpring.findAll();
    }
}
