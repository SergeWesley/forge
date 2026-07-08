package com.sergewesley.forge.dto.nominatim;

public record NominatimAddressResponse(
        String latitude, String longitude, String formattedAddress) {}
