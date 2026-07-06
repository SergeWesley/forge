package com.sergewesley.forge.external.dog;

import com.sergewesley.forge.dto.dog.DogResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DogService extends BaseExternalService {

    private static final String RANDOM_DOG_URL = "https://dog.ceo/api/breeds/image/random";

    public DogService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<String> getRandomDogImage() {
        return executeGetCall(
                RANDOM_DOG_URL,
                DogResponse.class,
                DogResponse::message,
                "Appel de l'API Dog CEO pour une image de chien aléatoire...",
                "Erreur lors de la récupération d'une image de chien depuis Dog CEO",
                log);
    }
}
