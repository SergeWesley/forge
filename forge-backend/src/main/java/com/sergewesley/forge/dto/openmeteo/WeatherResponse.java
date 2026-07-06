package com.sergewesley.forge.dto.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(@JsonProperty("current_weather") CurrentWeather currentWeather) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeather(double temperature, double windspeed) {}
}
