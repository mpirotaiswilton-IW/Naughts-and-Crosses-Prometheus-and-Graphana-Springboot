package com.max_pw_iw.naughtsandcrosses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
            .title("Naughts and Crosses")
            .description("An API that allows users to play  games of Naughts and crosses")
            .version("v1.0"));
    }
}
