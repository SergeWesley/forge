package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.nominatim.NominatimAddressResponse;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.external.nominatim.NominatimService;
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
@RequestMapping("/api/nominatim")
@Tag(name = "Nominatim", description = "api.nominatim.tag.description")
public class NominatimController {

    private final NominatimService nominatimService;
    private final MessageSource messageSource;

    public NominatimController(NominatimService nominatimService, MessageSource messageSource) {
        this.nominatimService = nominatimService;
        this.messageSource = messageSource;
    }

    @GetMapping("/geocode")
    @Operation(
            summary = "api.nominatim.geocode.summary",
            description = "api.nominatim.geocode.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public NominatimAddressResponse geocode(
            @Parameter(description = "api.nominatim.param.address", required = true) @RequestParam
                    String address) {
        return nominatimService
                .geocodeAddress(address)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        messageSource.getMessage(
                                                "error.nominatim.unavailable",
                                                new Object[] {address},
                                                LocaleContextHolder.getLocale())));
    }
}
