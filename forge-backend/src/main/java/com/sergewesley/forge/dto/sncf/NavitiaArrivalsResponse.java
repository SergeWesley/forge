package com.sergewesley.forge.dto.sncf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mapping brut de la réponse Navitia pour l'endpoint /arrivals. Chaque arrivée contient un
 * stop_date_time et des display_informations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NavitiaArrivalsResponse(List<Arrival> arrivals) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Arrival(
            @JsonProperty("stop_date_time") StopDateTime stopDateTime,
            @JsonProperty("display_informations") DisplayInformations displayInformations) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StopDateTime(
            @JsonProperty("arrival_date_time") String arrivalDatetime,
            @JsonProperty("base_arrival_date_time") String baseArrivalDatetime) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DisplayInformations(
            String direction,
            @JsonProperty("commercial_mode") String commercialMode,
            String network,
            String label,
            String color,
            String code) {}

    /** Convertit la réponse Navitia brute en une SncfArrivalResponse propre. */
    public static SncfArrivalResponse toArrivalResponse(NavitiaArrivalsResponse response) {
        if (response.arrivals() == null || response.arrivals().isEmpty()) {
            return null;
        }

        List<SncfArrivalDto> dtos =
                response.arrivals().stream()
                        .map(
                                arr -> {
                                    DisplayInformations info = arr.displayInformations();
                                    StopDateTime time = arr.stopDateTime();
                                    return new SncfArrivalDto(
                                            info != null ? info.direction() : null,
                                            info != null ? info.commercialMode() : null,
                                            info != null ? info.network() : null,
                                            info != null ? info.label() : null,
                                            info != null ? info.code() : null,
                                            info != null ? info.color() : null,
                                            time != null ? time.arrivalDatetime() : null,
                                            time != null ? time.baseArrivalDatetime() : null);
                                })
                        .toList();

        return new SncfArrivalResponse(dtos);
    }
}
