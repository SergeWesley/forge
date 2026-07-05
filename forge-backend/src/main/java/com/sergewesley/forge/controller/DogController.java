package com.sergewesley.forge.controller;

import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.external.dog.DogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dogs")
@Tag(name = "Dog API", description = "api.dog.tag.description")
public class DogController {

    private final DogService dogService;
    private final MessageSource messageSource;

    public DogController(DogService dogService, MessageSource messageSource) {
        this.dogService = dogService;
        this.messageSource = messageSource;
    }

    @GetMapping("/random")
    @Operation(
        summary = "api.dog.get.summary", 
        description = "api.dog.get.desc",
        tags = {"Generative UI"},
        extensions = {@Extension(name = "x-generative-ui", properties = {@ExtensionProperty(name = "enabled", value = "true")})}
    )
    public ResponseEntity<Map<String, String>> getRandomDog() {
        return dogService.getRandomDogImage()
                .map(imageUrl -> ResponseEntity.ok(Map.of("imageUrl", imageUrl)))
                .orElseThrow(() -> {
                    String errorMessage = messageSource.getMessage("error.dog.unavailable", null, LocaleContextHolder.getLocale());
                    return new ExternalServiceException(errorMessage);
                });
    }
}
