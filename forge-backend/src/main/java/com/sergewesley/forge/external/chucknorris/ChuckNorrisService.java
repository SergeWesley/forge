package com.sergewesley.forge.external.chucknorris;

import com.sergewesley.forge.dto.chucknorris.ChuckNorrisResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
public class ChuckNorrisService {

    private final RestTemplate restTemplate;

    public ChuckNorrisService() {
        this.restTemplate = new RestTemplate();
    }

    public Optional<String> getRandomJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        try {
            log.info("Appel de l'API Chuck Norris...");
            ChuckNorrisResponse response = restTemplate.getForObject(url, ChuckNorrisResponse.class);
            if (response != null && response.getValue() != null) {
                return Optional.of(response.getValue());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la blague Chuck Norris", e);
        }
        return Optional.empty();
    }
}
