package com.sergewesley.forge.dto.art;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ArtResponse(List<ArtData> data) {
    public record ArtData(
        Long id,
        String title,
        @JsonProperty("artist_title") String artistTitle,
        @JsonProperty("image_id") String imageId
    ) {}
}
