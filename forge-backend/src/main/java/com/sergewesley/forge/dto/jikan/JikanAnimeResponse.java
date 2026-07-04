package com.sergewesley.forge.dto.jikan;

public record JikanAnimeResponse(AnimeData data) {
    public record AnimeData(
            int mal_id,
            String url,
            String title,
            String synopsis,
            Images images
    ) {}

    public record Images(Jpg jpg) {}
    public record Jpg(String image_url) {}
}
