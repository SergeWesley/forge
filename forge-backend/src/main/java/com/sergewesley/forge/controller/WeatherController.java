package com.sergewesley.forge.controller;

import com.sergewesley.forge.external.openmeteo.OpenMeteoService;
import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.exception.ExternalServiceException;

import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather API", description = "api.weather.tag.description")
public class WeatherController {

    private final OpenMeteoService openMeteoService;
    private final MessageSource messageSource;

    public WeatherController(OpenMeteoService openMeteoService, MessageSource messageSource) {
        this.openMeteoService = openMeteoService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @Operation(summary = "api.weather.get.summary", description = "api.weather.get.desc")
    public ResponseEntity<?> getWeather(
            @Parameter(description = "api.weather.param.city", required = true, example = "Paris") @RequestParam String city) {

        // 1. Chercher la ville pour avoir ses coordonnées
        Optional<GeoResultResponse.GeoLocation> locationOpt = openMeteoService.findCity(city);
        if (locationOpt.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.city.notfound", new Object[]{city}, LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(errorMessage);
        }

        GeoResultResponse.GeoLocation location = locationOpt.get();

        // 2. Chercher la météo avec ces coordonnées
        Optional<WeatherResponse.CurrentWeather> weatherOpt = openMeteoService.getWeather(
                location.getLatitude(),
                location.getLongitude());

        if (weatherOpt.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.weather.unavailable", new Object[]{location.getName()}, LocaleContextHolder.getLocale());
            throw new ExternalServiceException(errorMessage);
        }

        return ResponseEntity.ok(weatherOpt.get());
    }
}
