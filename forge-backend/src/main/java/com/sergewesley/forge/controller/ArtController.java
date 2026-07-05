package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.art.ArtItem;
import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.external.art.ArtService;
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

import java.util.List;

@RestController
@RequestMapping("/api/art")
@Tag(name = "Art API", description = "api.art.tag.description")
public class ArtController {

    private final ArtService artService;
    private final MessageSource messageSource;

    public ArtController(ArtService artService, MessageSource messageSource) {
        this.artService = artService;
        this.messageSource = messageSource;
    }

    @GetMapping("/search")
    @Operation(
        summary = "api.art.search.summary", 
        description = "api.art.search.desc",
        tags = {"Generative UI"},
        extensions = {@Extension(name = "x-generative-ui", properties = {@ExtensionProperty(name = "enabled", value = "true")})}
    )
    public ResponseEntity<List<ArtItem>> searchArt(
            @Parameter(description = "api.art.param.query", required = true, example = "Van Gogh") 
            @RequestParam String query) {
            
        return artService.searchArt(query)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    String errorMessage = messageSource.getMessage("error.art.unavailable", null, LocaleContextHolder.getLocale());
                    return new ExternalServiceException(errorMessage);
                });
    }
}
