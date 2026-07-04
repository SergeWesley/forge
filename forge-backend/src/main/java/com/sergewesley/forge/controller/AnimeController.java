package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.jikan.JikanAnimeResponse;
import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.external.jikan.JikanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anime")
@Tag(name = "Anime API", description = "api.anime.tag.description")
public class AnimeController {

    private final JikanService jikanService;
    private final MessageSource messageSource;

    public AnimeController(JikanService jikanService, MessageSource messageSource) {
        this.jikanService = jikanService;
        this.messageSource = messageSource;
    }

    @GetMapping("/random")
    @Operation(summary = "api.anime.get.summary", description = "api.anime.get.desc")
    public ResponseEntity<JikanAnimeResponse.AnimeData> getRandomAnime() {
        return jikanService.getRandomAnime()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    String errorMessage = messageSource.getMessage("error.anime.unavailable", null, LocaleContextHolder.getLocale());
                    return new ExternalServiceException(errorMessage);
                });
    }
}
