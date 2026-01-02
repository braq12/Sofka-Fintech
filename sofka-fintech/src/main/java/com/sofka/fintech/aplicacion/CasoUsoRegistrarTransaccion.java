package com.sofka.fintech.aplicacion;

import java.math.BigDecimal;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import reactor.core.publisher.Mono;

public interface CasoUsoRegistrarTransaccion {
    Mono<RespuestaTransaccion> ejecutar(BigDecimal monto);
}
