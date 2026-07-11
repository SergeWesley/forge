package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.sncf.SncfArrivalResponse;
import com.sergewesley.forge.dto.sncf.SncfDepartureResponse;
import com.sergewesley.forge.dto.sncf.SncfStationDto;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.external.sncf.SncfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sncf")
@Tag(name = "SNCF", description = "api.sncf.tag.description")
public class SncfController {

    private final SncfService sncfService;
    private final MessageSource messageSource;

    public SncfController(SncfService sncfService, MessageSource messageSource) {
        this.sncfService = sncfService;
        this.messageSource = messageSource;
    }

    @GetMapping("/departures")
    @Operation(
            summary = "api.sncf.departures.summary",
            description = "api.sncf.departures.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public SncfDepartureResponse getDepartures(
            @Parameter(description = "api.sncf.param.station", required = true) @RequestParam
                    String station) {

        // 1. Résoudre le nom de la gare en ID Navitia
        SncfStationDto resolvedStation =
                sncfService
                        .searchStation(station)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                messageSource.getMessage(
                                                        "error.sncf.station.notfound",
                                                        new Object[] {station},
                                                        LocaleContextHolder.getLocale())));

        // 2. Récupérer les prochains départs pour cette gare
        return sncfService
                .getDepartures(resolvedStation.id())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        messageSource.getMessage(
                                                "error.sncf.departures.unavailable",
                                                new Object[] {station},
                                                LocaleContextHolder.getLocale())));
    }

    @GetMapping("/arrivals")
    @Operation(
            summary = "api.sncf.arrivals.summary",
            description = "api.sncf.arrivals.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public SncfArrivalResponse getArrivals(
            @Parameter(description = "api.sncf.param.station", required = true) @RequestParam
                    String station) {

        // 1. Résoudre le nom de la gare en ID Navitia
        SncfStationDto resolvedStation =
                sncfService
                        .searchStation(station)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                messageSource.getMessage(
                                                        "error.sncf.station.notfound",
                                                        new Object[] {station},
                                                        LocaleContextHolder.getLocale())));

        // 2. Récupérer les prochaines arrivées pour cette gare
        return sncfService
                .getArrivals(resolvedStation.id())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        messageSource.getMessage(
                                                "error.sncf.arrivals.unavailable",
                                                new Object[] {station},
                                                LocaleContextHolder.getLocale())));
    }
}
