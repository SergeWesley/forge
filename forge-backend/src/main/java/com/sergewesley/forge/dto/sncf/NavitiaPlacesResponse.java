package com.sergewesley.forge.dto.sncf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mapping brut de la réponse Navitia pour l'endpoint /places. Chaque "place" de type stop_area
 * contient un objet stop_area imbriqué.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NavitiaPlacesResponse(List<Place> places) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Place(
            String id,
            String name,
            @JsonProperty("embedded_type") String embeddedType,
            @JsonProperty("stop_area") StopArea stopArea) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StopArea(String id, String name, String label) {}

    /** Extrait le premier stop_area trouvé et le convertit en SncfStationDto. */
    public static SncfStationDto toFirstStation(NavitiaPlacesResponse response) {
        if (response.places() != null && !response.places().isEmpty()) {
            for (Place place : response.places()) {
                if ("stop_area".equals(place.embeddedType()) && place.stopArea() != null) {
                    StopArea sa = place.stopArea();
                    return new SncfStationDto(sa.id(), sa.name(), sa.label());
                }
            }
        }
        return null;
    }
}
