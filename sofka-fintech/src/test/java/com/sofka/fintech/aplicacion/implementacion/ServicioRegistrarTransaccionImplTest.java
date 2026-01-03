package com.sofka.fintech.aplicacion.implementacion;

import com.sofka.fintech.api.dto.RespuestaTransaccion;
import com.sofka.fintech.api.dto.SolicitudCrearTransaccion;
import com.sofka.fintech.dominio.PoliticaComision;
import com.sofka.fintech.dominio.Transaccion;
import com.sofka.fintech.dominio.excepciones.ExcepcionNegocio;
import com.sofka.fintech.puertos.salida.PublicadorTransacciones;
import com.sofka.fintech.puertos.salida.RepositorioTransaccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/* Test apoyado en IA */
class ServicioRegistrarTransaccionImplTest {

    private RepositorioTransaccion repositorioTransaccion;
    private PoliticaComision politicaComision;
    private PublicadorTransacciones publicadorTransacciones;

    private ServicioRegistrarTransaccionImpl servicio;

    @BeforeEach
    void setUp() {
        repositorioTransaccion = mock(RepositorioTransaccion.class);
        politicaComision = mock(PoliticaComision.class);
        publicadorTransacciones = mock(PublicadorTransacciones.class);

        servicio = new ServicioRegistrarTransaccionImpl(
                repositorioTransaccion,
                politicaComision,
                publicadorTransacciones
        );
    }

    @Test
    void debe_fallar_si_monto_es_nulo() {
        SolicitudCrearTransaccion solicitud = new SolicitudCrearTransaccion();
        solicitud.setMonto(null);
        solicitud.setDescripcion("pruebas");

        // OJO: en tu servicio llamas politicaComision.calcular(...) ANTES de validar null,
        // así que aquí para evitar NullPointer, definimos que el mock soporte null:
        when(politicaComision.calcular(isNull())).thenReturn(BigDecimal.ZERO);

        StepVerifier.create(servicio.ejecutar(solicitud))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(ExcepcionNegocio.class);
                    ExcepcionNegocio ex = (ExcepcionNegocio) err;
                    assertThat(ex.getCodigo()).isEqualTo("MONTO_INVALIDO");
                    assertThat(ex.getMessage()).contains("El monto debe ser mayor a cero");
                })
                .verify();

        verify(repositorioTransaccion, never()).guardar(any());
        verify(publicadorTransacciones, never()).publicar(any());
    }

    @Test
    void debe_fallar_si_monto_es_cero_o_menor() {
        SolicitudCrearTransaccion solicitud = new SolicitudCrearTransaccion();
        solicitud.setMonto(BigDecimal.ZERO);
        solicitud.setDescripcion("pruebas");

        when(politicaComision.calcular(BigDecimal.ZERO)).thenReturn(BigDecimal.ZERO);

        StepVerifier.create(servicio.ejecutar(solicitud))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(ExcepcionNegocio.class);
                    ExcepcionNegocio ex = (ExcepcionNegocio) err;
                    assertThat(ex.getCodigo()).isEqualTo("MONTO_INVALIDO");
                })
                .verify();

        verify(repositorioTransaccion, never()).guardar(any());
        verify(publicadorTransacciones, never()).publicar(any());
        verify(politicaComision, times(1)).calcular(BigDecimal.ZERO);
    }

    @Test
    void debe_registrar_y_publicar_transaccion_cuando_monto_valido() {
        SolicitudCrearTransaccion solicitud = new SolicitudCrearTransaccion();
        solicitud.setMonto(new BigDecimal("1000"));
        solicitud.setDescripcion("pruebas");

        BigDecimal comisionCalculada = new BigDecimal("20.00");
        when(politicaComision.calcular(new BigDecimal("1000"))).thenReturn(comisionCalculada);

        Transaccion guardada = new Transaccion();
        guardada.setIdTransaccion(17L);
        guardada.setMonto(new BigDecimal("1000"));
        guardada.setComision(comisionCalculada);
        guardada.setDescripcion("pruebas");
        guardada.setFechaRegistro(LocalDateTime.now());

        when(repositorioTransaccion.guardar(any(Transaccion.class)))
                .thenReturn(Mono.just(guardada));

        when(publicadorTransacciones.publicar(any(RespuestaTransaccion.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(servicio.ejecutar(solicitud))
                .assertNext(r -> {
                    assertThat(r.getIdTransaccion()).isEqualTo(17L);
                    assertThat(r.getMonto()).isEqualByComparingTo("1000");
                    assertThat(r.getComision()).isEqualByComparingTo("20.00");
                    assertThat(r.getDescripcion()).isEqualTo("pruebas");
                    assertThat(r.getFechaRegistro()).isNotNull();
                })
                .verifyComplete();

        // Verifica que se calculó comisión con el monto correcto
        verify(politicaComision, times(1)).calcular(new BigDecimal("1000"));

        // Verifica que se guardó transacción con campos correctos
        ArgumentCaptor<Transaccion> captor = ArgumentCaptor.forClass(Transaccion.class);
        verify(repositorioTransaccion, times(1)).guardar(captor.capture());

        Transaccion enviadaAGuardar = captor.getValue();
        assertThat(enviadaAGuardar.getMonto()).isEqualByComparingTo("1000");
        assertThat(enviadaAGuardar.getComision()).isEqualByComparingTo("20.00");
        assertThat(enviadaAGuardar.getDescripcion()).isEqualTo("pruebas");
        assertThat(enviadaAGuardar.getFechaRegistro()).isNotNull();

        // Verifica que se publicó
        verify(publicadorTransacciones, times(1)).publicar(any(RespuestaTransaccion.class));
    }
}
