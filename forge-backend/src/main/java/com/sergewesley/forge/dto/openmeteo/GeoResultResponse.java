package com.sergewesley.forge.dto.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoResultResponse {
    private List<GeoLocation> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoLocation {
        private String name;
        private double latitude;
        private double longitude;
        private String country;
    }
}
