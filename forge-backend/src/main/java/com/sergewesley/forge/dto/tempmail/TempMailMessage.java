package com.sergewesley.forge.dto.tempmail;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TempMailMessage(
        @JsonProperty("mail_id") String mailId,
        @JsonProperty("mail_from") String mailFrom,
        @JsonProperty("mail_subject") String mailSubject,
        @JsonProperty("mail_excerpt") String mailExcerpt,
        @JsonProperty("mail_body") String mailBody,
        @JsonProperty("mail_date") String mailDate,
        @JsonProperty("mail_timestamp") Long mailTimestamp) {}
