package com.sergewesley.forge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponse {
    private String output;
    private String type = "info";

    public CommandResponse(String output) {
        this.output = output;
        this.type = "info";
    }
}
