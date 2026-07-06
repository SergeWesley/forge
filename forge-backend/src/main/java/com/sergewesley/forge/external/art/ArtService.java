package com.sergewesley.forge.external.art;

import com.sergewesley.forge.dto.art.ArtItem;
import com.sergewesley.forge.dto.art.ArtResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ArtService extends BaseExternalService {

    private static final String BASE_URL = "https://api.artic.edu/api/v1/artworks/search";
    private static final String IMAGE_BASE_URL =
            "https://www.artic.edu/iiif/2/%s/full/843,/0/default.jpg";

    public ArtService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<List<ArtItem>> searchArt(String query) {
        String url =
                UriComponentsBuilder.fromUriString(BASE_URL)
                        .queryParam("q", query)
                        .queryParam("fields", "id,title,image_id,artist_title")
                        .toUriString();

        return executeGetCall(
                url,
                ArtResponse.class,
                this::mapToArtItems,
                "Appel de l'API Art Institute of Chicago pour la recherche : " + query,
                "Erreur lors de la recherche d'oeuvres d'art",
                log);
    }

    private List<ArtItem> mapToArtItems(ArtResponse response) {
        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }

        return response.data().stream()
                // Filtrer les oeuvres qui n'ont pas d'image
                .filter(data -> data.imageId() != null)
                .map(
                        data ->
                                new ArtItem(
                                        data.id(),
                                        data.title(),
                                        data.artistTitle() != null
                                                ? data.artistTitle()
                                                : "Unknown Artist",
                                        String.format(IMAGE_BASE_URL, data.imageId())))
                .collect(Collectors.toList());
    }
}
