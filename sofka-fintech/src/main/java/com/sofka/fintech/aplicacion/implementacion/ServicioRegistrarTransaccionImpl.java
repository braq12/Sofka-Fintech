package com.sofka.fintech.aplicacion.implementacion;

import java.math.BigDecimal;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.aplicacion.CasoUsoRegistrarTransaccion;
import com.sofka.fintech.dominio.PoliticaComision;
import com.sofka.fintech.dominio.Transaccion;
import com.sofka.fintech.puertos.salida.PublicadorTransacciones;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ServicioRegistrarTransaccionImpl implements CasoUsoRegistrarTransaccion {

    private final RepositorioTransaccion repositorioTransaccion;
    private final PoliticaComision politicaComision;
    private final PublicadorTransacciones publicadorTransacciones;

    public ServicioRegistrarTransaccionImpl(RepositorioTransaccion repositorioTransaccion,
                                            PoliticaComision politicaComision,
                                            PublicadorTransacciones publicadorTransacciones) {
        this.repositorioTransaccion = repositorioTransaccion;
        this.politicaComision = politicaComision;
        this.publicadorTransacciones = publicadorTransacciones;
    }

    @Override
    public Mono<RespuestaTransaccion> ejecutar(BigDecimal monto) {
        BigDecimal comision = politicaComision.calcular(monto);

        Transaccion transaccion = new Transaccion();
        transaccion.setMonto(monto);
        transaccion.setComision(comision);

        return repositorioTransaccion.guardar(transaccion)
                .map(t -> new RespuestaTransaccion(
                        t.getIdTransaccion(),
                        t.getMonto(),
                        t.getComision(),
                        t.getFechaRegistro()
                ))
                .flatMap(respuesta ->
                        publicadorTransacciones.publicar(respuesta).thenReturn(respuesta)
                );
    }
}
