package com.sergewesley.forge.external.openmeteo;

import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
public class OpenMeteoService {

    private final RestTemplate restTemplate;

    public OpenMeteoService() {
        this.restTemplate = new RestTemplate();
    }

    public Optional<GeoResultResponse.GeoLocation> findCity(String cityName) {
        String url = UriComponentsBuilder.fromUriString("https://geocoding-api.open-meteo.com/v1/search")
                .queryParam("name", cityName)
                .queryParam("count", 1)
                .queryParam("language", "en")
                .queryParam("format", "json")
                .toUriString();

        try {
            log.info("Recherche des coordonnées pour la ville : {}", cityName);
            GeoResultResponse response = restTemplate.getForObject(url, GeoResultResponse.class);
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                return Optional.of(response.getResults().get(0));
            }
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de la ville : " + cityName, e);
        }
        return Optional.empty();
    }

    public Optional<WeatherResponse.CurrentWeather> getWeather(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .toUriString();

        try {
            log.info("Récupération de la météo pour les coordonnées ({}, {})", latitude, longitude);
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            if (response != null && response.getCurrentWeather() != null) {
                return Optional.of(response.getCurrentWeather());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la météo", e);
        }
        return Optional.empty();
    }
}
