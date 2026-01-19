package com.sergewesley.forge.external.openmeteo;

import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

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
            GeoResultResponse response = restTemplate.getForObject(url, GeoResultResponse.class);
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                return Optional.of(response.getResults().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            if (response != null && response.getCurrentWeather() != null) {
                return Optional.of(response.getCurrentWeather());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
