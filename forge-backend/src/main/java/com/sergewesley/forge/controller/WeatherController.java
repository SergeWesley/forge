package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.external.openmeteo.OpenMeteoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(
            summary = "api.weather.get.summary",
            description = "api.weather.get.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public ResponseEntity<?> getWeather(
            @Parameter(description = "api.weather.param.city", required = true, example = "Paris")
                    @RequestParam
                    String city) {

        // 1. Chercher la ville pour avoir ses coordonnées
        GeoResultResponse.GeoLocation location =
                openMeteoService
                        .findCity(city)
                        .orElseThrow(
                                () -> {
                                    String errorMessage =
                                            messageSource.getMessage(
                                                    "error.city.notfound",
                                                    new Object[] {city},
                                                    LocaleContextHolder.getLocale());
                                    return new ResourceNotFoundException(errorMessage);
                                });

        // 2. Chercher la météo avec ces coordonnées
        return openMeteoService
                .getWeather(location.latitude(), location.longitude())
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> {
                            String errorMessage =
                                    messageSource.getMessage(
                                            "error.weather.unavailable",
                                            new Object[] {location.name()},
                                            LocaleContextHolder.getLocale());
                            return new ExternalServiceException(errorMessage);
                        });
    }
}
