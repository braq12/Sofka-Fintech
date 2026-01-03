package com.sofka.fintech.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class ConfiguracionCors {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuracion = new CorsConfiguration();

        configuracion.addAllowedOrigin("http://localhost:4200");
        configuracion.addAllowedMethod("*");
        configuracion.addAllowedHeader("*");
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);

        return new CorsWebFilter(fuente);
    }
}

