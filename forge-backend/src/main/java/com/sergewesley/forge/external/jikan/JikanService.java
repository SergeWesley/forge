package com.sergewesley.forge.external.jikan;

import com.sergewesley.forge.dto.jikan.JikanAnimeResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class JikanService extends BaseExternalService {

    private static final String RANDOM_ANIME_URL = "https://api.jikan.moe/v4/random/anime";

    public JikanService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<JikanAnimeResponse.AnimeData> getRandomAnime() {
        return executeGetCall(
                RANDOM_ANIME_URL,
                JikanAnimeResponse.class,
                JikanAnimeResponse::data,
                "Appel de l'API Jikan pour un anime aléatoire...",
                "Erreur lors de la récupération d'un anime aléatoire depuis Jikan",
                log);
    }
}
