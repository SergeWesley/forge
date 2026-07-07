package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.tempmail.TempEmailResponse;
import com.sergewesley.forge.dto.tempmail.TempMailListResponse;
import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.external.tempmail.TempMailService;
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
@RequestMapping("/api/tempmail")
@Tag(name = "Temporary Email API", description = "api.tempmail.tag.description")
public class TempMailController {

    private final TempMailService tempMailService;
    private final MessageSource messageSource;

    public TempMailController(TempMailService tempMailService, MessageSource messageSource) {
        this.tempMailService = tempMailService;
        this.messageSource = messageSource;
    }

    @GetMapping("/generate")
    @Operation(
            summary = "api.tempmail.generate.summary",
            description = "api.tempmail.generate.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public ResponseEntity<TempEmailResponse> generateTempEmail() {
        return tempMailService
                .generateTempEmail()
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> {
                            String errorMessage =
                                    messageSource.getMessage(
                                            "error.tempmail.unavailable",
                                            null,
                                            LocaleContextHolder.getLocale());
                            return new ExternalServiceException(errorMessage);
                        });
    }

    @GetMapping("/check")
    @Operation(
            summary = "api.tempmail.check.summary",
            description = "api.tempmail.check.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public ResponseEntity<TempMailListResponse> checkEmails(
            @Parameter(description = "Session token (sid_token)", required = true) @RequestParam
                    String sidToken) {
        return tempMailService
                .checkEmails(sidToken)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> {
                            String errorMessage =
                                    messageSource.getMessage(
                                            "error.tempmail.unavailable",
                                            null,
                                            LocaleContextHolder.getLocale());
                            return new ExternalServiceException(errorMessage);
                        });
    }
}
