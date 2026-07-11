package com.sergewesley.forge.external.sncf;

import com.sergewesley.forge.dto.sncf.NavitiaArrivalsResponse;
import com.sergewesley.forge.dto.sncf.NavitiaDeparturesResponse;
import com.sergewesley.forge.dto.sncf.NavitiaJourneysResponse;
import com.sergewesley.forge.dto.sncf.NavitiaPlacesResponse;
import com.sergewesley.forge.dto.sncf.SncfArrivalResponse;
import com.sergewesley.forge.dto.sncf.SncfDepartureResponse;
import com.sergewesley.forge.dto.sncf.SncfJourneyResponse;
import com.sergewesley.forge.dto.sncf.SncfStationDto;
import com.sergewesley.forge.external.BaseExternalService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service d'intégration avec l'API SNCF (basée sur Navitia). Gère l'authentification Basic HTTP et
 * les appels aux endpoints places et departures.
 */
@Slf4j
@Service
public class SncfService extends BaseExternalService {

    private static final String SNCF_BASE_URL = "https://api.sncf.com/v1/coverage/sncf";

    private final String apiKey;

    public SncfService(RestTemplate restTemplate, @Value("${sncf.api.key}") String apiKey) {
        super(restTemplate);
        this.apiKey = apiKey;
    }

    /**
     * Recherche une gare par nom via l'endpoint /places de Navitia.
     *
     * @param query Le nom de la gare recherchée (ex: "Paris Gare de Lyon")
     * @return La première gare (stop_area) correspondante
     */
    public Optional<SncfStationDto> searchStation(String query) {
        String url =
                UriComponentsBuilder.fromUriString(SNCF_BASE_URL + "/places")
                        .queryParam("q", query)
                        .queryParam("type[]", "stop_area")
                        .queryParam("count", 1)
                        .toUriString();

        return executeExchangeCall(
                url,
                HttpMethod.GET,
                buildAuthEntity(),
                NavitiaPlacesResponse.class,
                NavitiaPlacesResponse::toFirstStation,
                "Recherche de gare SNCF : " + query,
                "Erreur lors de la recherche de gare SNCF",
                log);
    }

    /**
     * Récupère les prochains départs pour une gare identifiée par son ID Navitia.
     *
     * @param stopAreaId L'identifiant Navitia de la gare (ex: "stop_area:SNCF:87686006")
     * @return La liste des prochains départs
     */
    public Optional<SncfDepartureResponse> getDepartures(String stopAreaId) {
        String url =
                UriComponentsBuilder.fromUriString(
                                SNCF_BASE_URL + "/stop_areas/" + stopAreaId + "/departures")
                        .queryParam("count", 10)
                        .toUriString();

        return executeExchangeCall(
                url,
                HttpMethod.GET,
                buildAuthEntity(),
                NavitiaDeparturesResponse.class,
                NavitiaDeparturesResponse::toDepartureResponse,
                "Récupération des départs pour la gare : " + stopAreaId,
                "Erreur lors de la récupération des départs SNCF",
                log);
    }

    /**
     * Récupère les prochaines arrivées pour une gare identifiée par son ID Navitia.
     *
     * @param stopAreaId L'identifiant Navitia de la gare (ex: "stop_area:SNCF:87686006")
     * @return La liste des prochaines arrivées
     */
    public Optional<SncfArrivalResponse> getArrivals(String stopAreaId) {
        String url =
                UriComponentsBuilder.fromUriString(
                                SNCF_BASE_URL + "/stop_areas/" + stopAreaId + "/arrivals")
                        .queryParam("count", 10)
                        .toUriString();

        return executeExchangeCall(
                url,
                HttpMethod.GET,
                buildAuthEntity(),
                NavitiaArrivalsResponse.class,
                NavitiaArrivalsResponse::toArrivalResponse,
                "Récupération des arrivées pour la gare : " + stopAreaId,
                "Erreur lors de la récupération des arrivées SNCF",
                log);
    }

    /**
     * Récupère les itinéraires entre deux gares à une date donnée.
     *
     * @param fromId L'identifiant Navitia de la gare de départ
     * @param toId L'identifiant Navitia de la gare d'arrivée
     * @param datetime La date au format Navitia (ex: 20260711T143000), optionnel
     * @param datetimeRepresents "departure" ou "arrival"
     */
    public Optional<SncfJourneyResponse> getJourneys(
            String fromId, String toId, String datetime, String datetimeRepresents) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(SNCF_BASE_URL + "/journeys")
                        .queryParam("from", fromId)
                        .queryParam("to", toId);

        if (datetime != null && !datetime.isEmpty()) {
            builder.queryParam("datetime", datetime);
        }
        if (datetimeRepresents != null && !datetimeRepresents.isEmpty()) {
            builder.queryParam("datetime_represents", datetimeRepresents);
        }

        return executeExchangeCall(
                builder.toUriString(),
                HttpMethod.GET,
                buildAuthEntity(),
                NavitiaJourneysResponse.class,
                NavitiaJourneysResponse::toJourneyResponse,
                "Recherche d'itinéraire SNCF de " + fromId + " à " + toId,
                "Erreur lors de la recherche d'itinéraire SNCF",
                log);
    }

    /**
     * Construit un HttpEntity avec le header d'authentification Basic HTTP. L'API SNCF utilise la
     * clé API comme username et un mot de passe vide.
     */
    private HttpEntity<String> buildAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        String credentials = apiKey + ":";
        String encodedCredentials =
                Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedCredentials);
        return new HttpEntity<>(headers);
    }
}
