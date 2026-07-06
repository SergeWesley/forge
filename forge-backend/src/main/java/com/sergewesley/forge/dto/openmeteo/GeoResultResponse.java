package com.sergewesley.forge.dto.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoResultResponse(List<GeoLocation> results) {
    public GeoLocation getFirstResult() {
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoLocation(String name, double latitude, double longitude, String country) {}
}
