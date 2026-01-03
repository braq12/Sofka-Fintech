package com.sofka.fintech.api;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.api.dto.SolicitudCrearTransaccion;
import com.sofka.fintech.aplicacion.CasoUsoListarTransacciones;
import com.sofka.fintech.aplicacion.CasoUsoRegistrarTransaccion;
import com.sofka.fintech.puertos.salida.PublicadorTransacciones;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ControladorTransaccion.class)
class ControladorTransaccionTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CasoUsoRegistrarTransaccion casoUsoRegistrarTransaccion;

    @MockitoBean
    private CasoUsoListarTransacciones casoUsoListarTransacciones;

    @MockitoBean
    private PublicadorTransacciones publicadorTransacciones;

    @Test
    void debe_registrar_transaccion_y_retornar_201() {
        SolicitudCrearTransaccion solicitud = new SolicitudCrearTransaccion();
        solicitud.setMonto(new BigDecimal("1000"));
        solicitud.setDescripcion("pruebas");

        RespuestaTransaccion respuesta = new RespuestaTransaccion(
                17L,
                new BigDecimal("1000"),
                new BigDecimal("20.00"),
                LocalDateTime.now(),
                "pruebas"
        );

        when(casoUsoRegistrarTransaccion.ejecutar(any(SolicitudCrearTransaccion.class)))
                .thenReturn(Mono.just(respuesta));

        webTestClient.post()
                .uri("/api/transacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitud)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.idTransaccion").isEqualTo(17)
                .jsonPath("$.monto").isEqualTo(1000)
                .jsonPath("$.comision").isEqualTo(20.00)
                .jsonPath("$.descripcion").isEqualTo("pruebas");
    }

    @Test
    void debe_listar_transacciones_y_retornar_200() {
        RespuestaTransaccion t1 = new RespuestaTransaccion(
                1L,
                new BigDecimal("100"),
                new BigDecimal("2.00"),
                LocalDateTime.now(),
                "a"
        );

        RespuestaTransaccion t2 = new RespuestaTransaccion(
                2L,
                new BigDecimal("200"),
                new BigDecimal("4.00"),
                LocalDateTime.now(),
                "b"
        );

        when(casoUsoListarTransacciones.ejecutar()).thenReturn(Flux.just(t1, t2));

        webTestClient.get()
                .uri("/api/transacciones")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].idTransaccion").isEqualTo(1)
                .jsonPath("$[0].descripcion").isEqualTo("a")
                .jsonPath("$[1].idTransaccion").isEqualTo(2)
                .jsonPath("$[1].descripcion").isEqualTo("b");
    }

    @Test
    void debe_exponer_stream_sse_con_evento_y_datos() {
        RespuestaTransaccion t = new RespuestaTransaccion(
                3L,
                new BigDecimal("500"),
                new BigDecimal("10.00"),
                LocalDateTime.of(2026, 1, 2, 21, 51, 54),
                "pruebas"
        );

        when(publicadorTransacciones.flujo()).thenReturn(Flux.just(t));

        // Para SSE es más confiable validar el texto crudo del stream
        webTestClient.get()
                .uri("/api/transacciones/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    // Debe incluir el nombre del evento y el JSON con campos clave
                    // Formato SSE: event: ... \n data: ... \n\n
                    org.assertj.core.api.Assertions.assertThat(body).contains("event:transaccion_registrada");
                    org.assertj.core.api.Assertions.assertThat(body).contains("\"idTransaccion\":3");
                    org.assertj.core.api.Assertions.assertThat(body).contains("\"descripcion\":\"pruebas\"");
                });
    }
}
