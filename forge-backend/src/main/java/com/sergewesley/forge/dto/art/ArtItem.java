package com.sergewesley.forge.dto.art;

public record ArtItem(
    Long id,
    String title,
    String artist,
    String imageUrl
) {}
