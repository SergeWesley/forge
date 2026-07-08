package com.sergewesley.forge.dto.nominatim;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NominatimResponse(
        String lat, String lon, @JsonProperty("display_name") String displayName) {
    public static NominatimAddressResponse toAddressResponse(NominatimResponse[] items) {
        if (items != null && items.length > 0) {
            NominatimResponse first = items[0];
            return new NominatimAddressResponse(first.lat(), first.lon(), first.displayName());
        }
        return null;
    }
}
