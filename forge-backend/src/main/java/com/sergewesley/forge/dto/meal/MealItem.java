package com.sergewesley.forge.dto.meal;

import java.util.List;

public record MealItem(
        String id,
        String name,
        String category,
        String area,
        String instructions,
        String imageUrl,
        String youtubeUrl,
        List<Ingredient> ingredients
) {
    public record Ingredient(String name, String measure) {}
}
