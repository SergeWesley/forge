package com.sergewesley.forge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // En créant ce Bean, Spring va créer une seule instance de RestTemplate
        // au démarrage de l'application et la distribuera partout où elle est demandée.
        return new RestTemplate();
    }
}
