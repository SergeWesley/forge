package com.sergewesley.forge.external.openmeteo;

import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;
import com.sergewesley.forge.external.BaseExternalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Service
public class OpenMeteoService extends BaseExternalService {

    public OpenMeteoService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<GeoResultResponse.GeoLocation> findCity(String cityName) {
        String url = UriComponentsBuilder.fromUriString("https://geocoding-api.open-meteo.com/v1/search")
                .queryParam("name", cityName)
                .queryParam("count", 1)
                .queryParam("language", "fr")
                .queryParam("format", "json")
                .toUriString();

        return executeGetCall(
                url,
                GeoResultResponse.class,
                response -> (response.getResults() != null && !response.getResults().isEmpty())
                        ? response.getResults().get(0)
                        : null,
                "Recherche des coordonnées pour la ville : " + cityName,
                "Erreur lors de la recherche de la ville : " + cityName,
                log);
    }

    public Optional<WeatherResponse.CurrentWeather> getWeather(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .toUriString();

        return executeGetCall(
                url,
                WeatherResponse.class,
                WeatherResponse::getCurrentWeather,
                String.format("Récupération de la météo pour les coordonnées (%s, %s)", latitude, longitude),
                "Erreur lors de la récupération de la météo",
                log);
    }
}
