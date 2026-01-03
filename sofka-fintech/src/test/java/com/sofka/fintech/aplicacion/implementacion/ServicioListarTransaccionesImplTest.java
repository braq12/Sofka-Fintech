package com.sofka.fintech.aplicacion.implementacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.dominio.Transaccion;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ServicioListarTransaccionesImplTest {

    private RepositorioTransaccion repositorioTransaccion;
    private ServicioListarTransaccionesImpl servicio;

    @BeforeEach
    void setUp() {
        repositorioTransaccion = mock(RepositorioTransaccion.class);
        servicio = new ServicioListarTransaccionesImpl(repositorioTransaccion);
    }

    @Test
    void debe_listar_y_mapear_transacciones_a_respuesta() {
        LocalDateTime ahora = LocalDateTime.now();

        Transaccion t1 = new Transaccion();
        t1.setIdTransaccion(1L);
        t1.setMonto(new BigDecimal("100"));
        t1.setComision(new BigDecimal("2.00"));
        t1.setFechaRegistro(ahora);
        t1.setDescripcion("a");

        Transaccion t2 = new Transaccion();
        t2.setIdTransaccion(2L);
        t2.setMonto(new BigDecimal("200"));
        t2.setComision(new BigDecimal("10.00"));
        t2.setFechaRegistro(ahora.plusMinutes(1));
        t2.setDescripcion("b");

        when(repositorioTransaccion.listar()).thenReturn(Flux.just(t1, t2));

        StepVerifier.create(servicio.ejecutar())
                .assertNext(r -> {
                    assertThat(r).isInstanceOf(RespuestaTransaccion.class);
                    assertThat(r.getIdTransaccion()).isEqualTo(1L);
                    assertThat(r.getMonto()).isEqualByComparingTo("100");
                    assertThat(r.getComision()).isEqualByComparingTo("2.00");
                    assertThat(r.getFechaRegistro()).isEqualTo(ahora);
                    assertThat(r.getDescripcion()).isEqualTo("a");
                })
                .assertNext(r -> {
                    assertThat(r.getIdTransaccion()).isEqualTo(2L);
                    assertThat(r.getMonto()).isEqualByComparingTo("200");
                    assertThat(r.getComision()).isEqualByComparingTo("10.00");
                    assertThat(r.getFechaRegistro()).isEqualTo(ahora.plusMinutes(1));
                    assertThat(r.getDescripcion()).isEqualTo("b");
                })
                .verifyComplete();

        verify(repositorioTransaccion, times(1)).listar();
        verifyNoMoreInteractions(repositorioTransaccion);
    }

    @Test
    void debe_retornar_flux_vacio_si_no_hay_registros() {
        when(repositorioTransaccion.listar()).thenReturn(Flux.empty());

        StepVerifier.create(servicio.ejecutar())
                .expectNextCount(0)
                .verifyComplete();

        verify(repositorioTransaccion, times(1)).listar();
        verifyNoMoreInteractions(repositorioTransaccion);
    }
}
