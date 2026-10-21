package com.ecommerce.admin.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI adminApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce admin API")
                        .version("v1")
                        .description("""
                                Products, categories, stock, users and roles for the admin panel, plus the public catalog.

                                To call protected endpoints: run POST /api/auth/login, copy the accessToken, \
                                click Authorize and paste it. Write endpoints return a human-readable message \
                                in the X-Message response header (URL-encoded).
                                """))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
