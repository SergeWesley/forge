package com.sergewesley.forge.external.chucknorris;

import com.sergewesley.forge.dto.chucknorris.ChuckNorrisResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ChuckNorrisService extends BaseExternalService {

    public ChuckNorrisService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<String> getRandomJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        return executeGetCall(
                url,
                ChuckNorrisResponse.class,
                ChuckNorrisResponse::value,
                "Récupération d'une blague Chuck Norris",
                "Erreur lors de la récupération de la blague Chuck Norris",
                log);
    }
}
