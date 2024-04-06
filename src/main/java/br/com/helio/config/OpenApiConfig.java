package br.com.helio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java 21 and Spring Boot 3")
                        .version("v1")
                        .description("REST with Spring Boot and Java")
                        .termsOfService("https://github.com/HelioOrtega")
                        .license(
                                new License()
                                        .name("Apache 2.0")
                                        .url("https://github.com/HelioOrtega")
                        )
                );
    }

}
