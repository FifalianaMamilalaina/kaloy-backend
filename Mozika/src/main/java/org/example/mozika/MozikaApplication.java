package org.example.mozika;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info; 
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;




@EnableScheduling
@SpringBootApplication
public class MozikaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MozikaApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                    .info(new Info().title("Mozika")
                    .description("Project Description")
                    .version("0.0.1-SNAPSHOT"));
    }
}
