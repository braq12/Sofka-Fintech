package com.sofka.fintech.api.errores;

import com.sofka.fintech.dominio.excepciones.ExcepcionNegocio;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(-2)
public class ManejadorGlobalErrores {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(WebExchangeBindException ex) {
        List<String> detalles = ex.getFieldErrors().stream()
                .map(this::formatearErrorCampo)
                .collect(Collectors.toList());

        RespuestaError respuesta = new RespuestaError(
                "VALIDACION",
                "La solicitud contiene datos inválidos",
                detalles,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<RespuestaError> manejarNegocio(ExcepcionNegocio ex) {
        RespuestaError respuesta = new RespuestaError(
                ex.getCodigo(),
                ex.getMessage(),
                List.of(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarGeneral(Exception ex) {
        RespuestaError respuesta = new RespuestaError(
                "ERROR_GENERAL",
                "Ocurrió un error inesperado",
                List.of(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }

    private String formatearErrorCampo(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
