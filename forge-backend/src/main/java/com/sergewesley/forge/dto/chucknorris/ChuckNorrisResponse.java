package com.sergewesley.forge.dto.chucknorris;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChuckNorrisResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("url")
    private String url;
}
