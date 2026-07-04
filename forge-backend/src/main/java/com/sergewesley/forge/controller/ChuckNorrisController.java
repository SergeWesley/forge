package com.sergewesley.forge.controller;

import com.sergewesley.forge.external.chucknorris.ChuckNorrisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.sergewesley.forge.exception.ExternalServiceException;

@RestController
@RequestMapping("/api/jokes")
@Tag(name = "Chuck Norris API", description = "api.chuck.tag.description")
public class ChuckNorrisController {

    private final ChuckNorrisService chuckNorrisService;
    private final MessageSource messageSource;

    public ChuckNorrisController(ChuckNorrisService chuckNorrisService, MessageSource messageSource) {
        this.chuckNorrisService = chuckNorrisService;
        this.messageSource = messageSource;
    }

    @GetMapping("/random")
    @Operation(summary = "api.chuck.get.summary", description = "api.chuck.get.desc")
    public ResponseEntity<String> getRandomJoke() {

        return chuckNorrisService.getRandomJoke()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    String errorMessage = messageSource.getMessage("error.joke.unavailable", null,
                            LocaleContextHolder.getLocale());
                    return new ExternalServiceException(errorMessage);
                });
    }
}
