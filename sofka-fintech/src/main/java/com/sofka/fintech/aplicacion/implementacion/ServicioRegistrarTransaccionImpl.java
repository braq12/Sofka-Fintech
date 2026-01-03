package com.sofka.fintech.aplicacion.implementacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.api.dto.SolicitudCrearTransaccion;
import com.sofka.fintech.aplicacion.CasoUsoRegistrarTransaccion;
import com.sofka.fintech.dominio.PoliticaComision;
import com.sofka.fintech.dominio.Transaccion;
import com.sofka.fintech.dominio.excepciones.ExcepcionNegocio;
import com.sofka.fintech.puertos.salida.PublicadorTransacciones;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public Mono<RespuestaTransaccion> ejecutar(SolicitudCrearTransaccion solicitud) {
        BigDecimal comision = politicaComision.calcular(solicitud.getMonto());

        Transaccion transaccion = new Transaccion();
        transaccion.setMonto(solicitud.getMonto());
        transaccion.setComision(comision);
        transaccion.setDescripcion(solicitud.getDescripcion());
        transaccion.setFechaRegistro(LocalDateTime.now());


        if (solicitud.getMonto() == null || solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new ExcepcionNegocio("MONTO_INVALIDO", "El monto debe ser mayor a cero"));
        }


        return repositorioTransaccion.guardar(transaccion)
                .map(t -> new RespuestaTransaccion(
                        t.getIdTransaccion(),
                        t.getMonto(),
                        t.getComision(),
                        t.getFechaRegistro(),
                        t.getDescripcion()
                ))
                .flatMap(respuesta ->
                        publicadorTransacciones.publicar(respuesta).thenReturn(respuesta)
                );
    }
}
