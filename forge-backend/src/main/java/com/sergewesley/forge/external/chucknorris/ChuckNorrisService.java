package com.sergewesley.forge.external.chucknorris;

import com.sergewesley.forge.dto.chucknorris.ChuckNorrisResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ChuckNorrisService {

    private final RestTemplate restTemplate;

    public ChuckNorrisService() {
        this.restTemplate = new RestTemplate();
    }

    public Optional<String> getRandomJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        try {
            ChuckNorrisResponse response = restTemplate.getForObject(url, ChuckNorrisResponse.class);
            if (response != null && response.getValue() != null) {
                return Optional.of(response.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
