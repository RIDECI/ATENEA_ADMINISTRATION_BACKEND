package edu.dosw.rideci.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API de RideECI
 * Provee la configuración para la documentación automática de los endpoints
 *
 * @author RideECI
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura la documentación OpenAPI para el módulo de administración
     *
     * @return Configuración personalizada de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ADMINISTRATION INSTITUTIONAL")
                        .version("1.0")
                        .description("Supervisa reportes y viajes, ademas de validar conductores y suspender o bloquear usuarios"));
    }
}