package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.mcu.McuCountdownResponse;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.external.mcu.McuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcu")
@Tag(name = "MCU", description = "api.mcu.tag.description")
public class McuController {

    private final McuService mcuService;
    private final MessageSource messageSource;

    public McuController(McuService mcuService, MessageSource messageSource) {
        this.mcuService = mcuService;
        this.messageSource = messageSource;
    }

    @GetMapping("/next")
    @Operation(
            summary = "api.mcu.next.summary",
            description = "api.mcu.next.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public McuCountdownResponse getNextMcuFilm() {
        return mcuService
                .getNextMcuFilm()
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        messageSource.getMessage(
                                                "error.mcu.unavailable",
                                                null,
                                                LocaleContextHolder.getLocale())));
    }
}
