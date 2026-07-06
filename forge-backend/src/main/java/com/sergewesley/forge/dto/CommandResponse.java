package com.sergewesley.forge.dto;

public record CommandResponse(String output, String type) {
    public CommandResponse(String output) {
        this(output, "info");
    }
}
