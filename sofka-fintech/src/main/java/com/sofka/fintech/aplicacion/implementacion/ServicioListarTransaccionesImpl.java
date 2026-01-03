package com.sofka.fintech.aplicacion.implementacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.aplicacion.CasoUsoListarTransacciones;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ServicioListarTransaccionesImpl implements CasoUsoListarTransacciones {

    private final RepositorioTransaccion repositorioTransaccion;

    public ServicioListarTransaccionesImpl(RepositorioTransaccion repositorioTransaccion) {
        this.repositorioTransaccion = repositorioTransaccion;
    }

    @Override
    public Flux<RespuestaTransaccion> ejecutar() {
        return repositorioTransaccion.listar()
                .map(t -> new RespuestaTransaccion(
                        t.getIdTransaccion(),
                        t.getMonto(),
                        t.getComision(),
                        t.getFechaRegistro(),
                        t.getDescripcion()
                ));
    }
}
