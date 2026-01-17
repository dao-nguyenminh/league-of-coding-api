package com.leagueofcoding.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenApiConfig - Swagger/OpenAPI configuration.
 *
 * @author dao-nguyenminh
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("League Of Coding API")
                        .version("0.1.0")
                        .description("Competitive 1v1 Coding Battle Platform - Backend API")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Authentication")
                                .description("User authentication APIs"),
                        new Tag()
                                .name("Problems")
                                .description("Public problem browsing APIs"),
                        new Tag()
                                .name("Admin - Problems")
                                .description("Problem management APIs (Admin only)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT authentication token")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}