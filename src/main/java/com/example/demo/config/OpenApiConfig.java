package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadata general que aparece en Swagger UI (título, descripción, versión)
 * y definición del esquema de seguridad JWT (botón "Authorize").
 * La documentación de cada endpoint puntual va con @Operation en el
 * controller correspondiente.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catálogo, Favoritos y Listas")
                        .description("Catálogo de productos (consumo de una API externa), favoritos y listas "
                                + "con persistencia en PostgreSQL. Autenticación con JWT: los endpoints de "
                                + "/api/auth y /api/productos son públicos; /api/listas y /api/favoritos "
                                + "requieren un token Bearer.")
                        .version("v2"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}