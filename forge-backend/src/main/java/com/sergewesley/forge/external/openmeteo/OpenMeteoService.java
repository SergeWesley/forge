package com.sergewesley.forge.external.openmeteo;

import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class OpenMeteoService extends BaseExternalService {

    public OpenMeteoService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<GeoResultResponse.GeoLocation> findCity(String cityName) {
        String url =
                UriComponentsBuilder.fromUriString("https://geocoding-api.open-meteo.com/v1/search")
                        .queryParam("name", cityName)
                        .queryParam("count", 1)
                        .queryParam("language", "fr")
                        .queryParam("format", "json")
                        .toUriString();

        return executeGetCall(
                url,
                GeoResultResponse.class,
                GeoResultResponse::getFirstResult,
                "Recherche de coordonnées pour : " + cityName,
                "Erreur lors du géocodage",
                log);
    }

    public Optional<WeatherResponse.CurrentWeather> getWeather(double latitude, double longitude) {
        String url =
                UriComponentsBuilder.fromUriString("https://api.open-meteo.com/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current_weather", true)
                        .toUriString();

        return executeGetCall(
                url,
                WeatherResponse.class,
                WeatherResponse::currentWeather,
                String.format(
                        "Récupération de la météo pour les coordonnées (%s, %s)",
                        latitude, longitude),
                "Erreur lors de la récupération de la météo",
                log);
    }
}
