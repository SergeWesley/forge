package com.sergewesley.forge.dto.tempmail;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TempMailListResponse(
        @JsonProperty("list") List<TempMailMessage> list,
        @JsonProperty("count") String count,
        @JsonProperty("email") String email,
        @JsonProperty("sid_token") String sidToken) {}
