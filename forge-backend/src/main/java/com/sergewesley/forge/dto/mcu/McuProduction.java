package com.sergewesley.forge.dto.mcu;

import com.fasterxml.jackson.annotation.JsonProperty;

public record McuProduction(
        Integer id,
        String title,
        String type,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("days_until") Integer daysUntil,
        String overview,
        @JsonProperty("poster_url") String posterUrl) {}
