package com.sergewesley.forge.external.meal;

import com.sergewesley.forge.dto.meal.Ingredient;
import com.sergewesley.forge.dto.meal.MealItem;
import com.sergewesley.forge.dto.meal.MealResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class MealService extends BaseExternalService {

    private static final String SEARCH_URL = "https://www.themealdb.com/api/json/v1/1/search.php";

    public MealService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<List<MealItem>> searchMeals(String query) {
        String url =
                UriComponentsBuilder.fromUriString(SEARCH_URL).queryParam("s", query).toUriString();

        return executeGetCall(
                url,
                MealResponse.class,
                this::mapToMealItems,
                "Appel de l'API TheMealDB pour la recherche : " + query,
                "Erreur lors de la recherche de recettes",
                log);
    }

    private List<MealItem> mapToMealItems(MealResponse response) {
        if (response == null || response.meals() == null) {
            return Collections.emptyList();
        }

        return response.meals().stream()
                .map(
                        mealMap -> {
                            List<Ingredient> ingredients = new ArrayList<>();
                            for (int i = 1; i <= 20; i++) {
                                String ingredient = mealMap.get("strIngredient" + i);
                                String measure = mealMap.get("strMeasure" + i);

                                if (ingredient != null && !ingredient.trim().isEmpty()) {
                                    ingredients.add(
                                            new Ingredient(
                                                    ingredient.trim(),
                                                    measure != null ? measure.trim() : ""));
                                }
                            }

                            return new MealItem(
                                    mealMap.get("idMeal"),
                                    mealMap.get("strMeal"),
                                    mealMap.get("strCategory"),
                                    mealMap.get("strArea"),
                                    mealMap.get("strInstructions"),
                                    mealMap.get("strMealThumb"),
                                    mealMap.get("strYoutube"),
                                    ingredients);
                        })
                .collect(Collectors.toList());
    }
}
