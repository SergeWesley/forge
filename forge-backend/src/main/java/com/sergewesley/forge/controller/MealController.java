package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.meal.MealItem;
import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.external.meal.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meal API", description = "api.meal.tag.description")
public class MealController {

    private final MealService mealService;
    private final MessageSource messageSource;

    public MealController(MealService mealService, MessageSource messageSource) {
        this.mealService = mealService;
        this.messageSource = messageSource;
    }

    @GetMapping("/search")
    @Operation(
            summary = "api.meal.search.summary",
            description = "api.meal.search.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public ResponseEntity<List<MealItem>> searchMeals(
            @Parameter(description = "api.meal.param.query", required = true, example = "Chicken")
                    @RequestParam
                    String query) {

        return mealService
                .searchMeals(query)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> {
                            String errorMessage =
                                    messageSource.getMessage(
                                            "error.meal.unavailable",
                                            null,
                                            LocaleContextHolder.getLocale());
                            return new ExternalServiceException(errorMessage);
                        });
    }
}
