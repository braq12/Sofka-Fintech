package com.sofka.fintech.api;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.api.dto.SolicitudCrearTransaccion;
import com.sofka.fintech.aplicacion.CasoUsoRegistrarTransaccion;

import com.sofka.fintech.puertos.salida.PublicadorTransacciones;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transacciones")
public class ControladorTransaccion {

    private final CasoUsoRegistrarTransaccion casoUsoRegistrarTransaccion;
    private final PublicadorTransacciones publicadorTransacciones;

    public ControladorTransaccion(CasoUsoRegistrarTransaccion casoUsoRegistrarTransaccion,
                                  PublicadorTransacciones publicadorTransacciones) {
        this.casoUsoRegistrarTransaccion = casoUsoRegistrarTransaccion;
        this.publicadorTransacciones = publicadorTransacciones;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RespuestaTransaccion> registrar(@Valid @RequestBody SolicitudCrearTransaccion solicitud) {
        return casoUsoRegistrarTransaccion.ejecutar(solicitud.getMonto());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<RespuestaTransaccion>> stream() {
        return publicadorTransacciones.flujo()
                .map(t -> ServerSentEvent.<RespuestaTransaccion>builder()
                        .event("transaccion_registrada")
                        .data(t)
                        .build()
                );
    }
}
