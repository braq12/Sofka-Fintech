package com.sofka.fintech.infraestructura.persistencia;

import com.sofka.fintech.dominio.Transaccion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RepositorioTransaccionR2dbcSpring extends ReactiveCrudRepository<Transaccion, Long> {
}
