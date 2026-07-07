package com.sergewesley.forge.dto.tempmail;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TempEmailResponse(
        @JsonProperty("email_addr") String emailAddress,
        @JsonProperty("sid_token") String sidToken,
        @JsonProperty("alias") String alias) {}
