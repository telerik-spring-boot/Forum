package com.telerik.forum.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Forum Application API")
                        .version("1.0.0")
                        .description("This is the API documentation for Forum Application.")
                        .contact(new Contact()
                                .name("Yordan, Nikolai, and the API Team")
                                .email("team@example.com")
                                .url("www.google.com")));
    }
}
