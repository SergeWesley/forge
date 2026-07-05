package com.sergewesley.forge.dto.meal;

import java.util.List;
import java.util.Map;

public record MealResponse(List<Map<String, String>> meals) {}
