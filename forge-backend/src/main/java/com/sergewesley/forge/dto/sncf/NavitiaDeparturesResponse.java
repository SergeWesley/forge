package com.sergewesley.forge.dto.sncf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mapping brut de la réponse Navitia pour l'endpoint /departures. Chaque départ contient un
 * stop_date_time et des display_informations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NavitiaDeparturesResponse(List<Departure> departures) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Departure(
            @JsonProperty("stop_date_time") StopDateTime stopDateTime,
            @JsonProperty("display_informations") DisplayInformations displayInformations) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StopDateTime(
            @JsonProperty("departure_date_time") String departureDatetime,
            @JsonProperty("base_departure_date_time") String baseDepartureDatetime) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DisplayInformations(
            String direction,
            @JsonProperty("commercial_mode") String commercialMode,
            String network,
            String label,
            String color,
            String code) {}

    /**
     * Convertit la réponse Navitia brute en une SncfDepartureResponse propre contenant le nom de la
     * gare et la liste des prochains départs.
     */
    public static SncfDepartureResponse toDepartureResponse(NavitiaDeparturesResponse response) {
        if (response.departures() == null || response.departures().isEmpty()) {
            return null;
        }

        List<SncfDepartureDto> dtos =
                response.departures().stream()
                        .map(
                                dep -> {
                                    DisplayInformations info = dep.displayInformations();
                                    StopDateTime time = dep.stopDateTime();
                                    return new SncfDepartureDto(
                                            info != null ? info.direction() : null,
                                            info != null ? info.commercialMode() : null,
                                            info != null ? info.network() : null,
                                            info != null ? info.label() : null,
                                            info != null ? info.code() : null,
                                            info != null ? info.color() : null,
                                            time != null ? time.departureDatetime() : null,
                                            time != null ? time.baseDepartureDatetime() : null);
                                })
                        .toList();

        return new SncfDepartureResponse(dtos);
    }
}
