package com.sergewesley.forge.dto.chucknorris;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChuckNorrisResponse(
        @JsonProperty("id") String id,
        @JsonProperty("value") String value,
        @JsonProperty("url") String url) {}
