package com.sergewesley.forge.dto.sncf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mapping brut de la réponse Navitia pour l'endpoint /journeys. Chaque journey contient des
 * sections (étapes du trajet), les heures de départ/arrivée, la durée totale, etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NavitiaJourneysResponse(List<Journey> journeys) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Journey(
            int duration,
            @JsonProperty("nb_transfers") int nbTransfers,
            @JsonProperty("departure_date_time") String departureDatetime,
            @JsonProperty("arrival_date_time") String arrivalDatetime,
            @JsonProperty("co2_emission") Co2Emission co2Emission,
            List<Section> sections) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Co2Emission(double value, String unit) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Section(
            String type,
            int duration,
            @JsonProperty("departure_date_time") String departureDatetime,
            @JsonProperty("arrival_date_time") String arrivalDatetime,
            Place from,
            Place to,
            @JsonProperty("display_informations") DisplayInformations displayInformations,
            @JsonProperty("geojson") GeoJson geoJson) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Place(String name, @JsonProperty("stop_point") StopPoint stopPoint) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StopPoint(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DisplayInformations(
            String direction,
            @JsonProperty("commercial_mode") String commercialMode,
            String network,
            String label,
            String color,
            String code) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoJson(String type) {}

    /** Convertit la réponse Navitia brute en une SncfJourneyResponse propre. */
    public static SncfJourneyResponse toJourneyResponse(NavitiaJourneysResponse response) {
        if (response.journeys() == null || response.journeys().isEmpty()) {
            return null;
        }

        List<SncfJourneyDto> dtos =
                response.journeys().stream()
                        .map(
                                journey -> {
                                    List<SncfJourneySectionDto> sectionDtos =
                                            journey.sections() != null
                                                    ? journey.sections().stream()
                                                            .map(
                                                                    NavitiaJourneysResponse
                                                                            ::mapSection)
                                                            .toList()
                                                    : List.of();

                                    return new SncfJourneyDto(
                                            journey.duration(),
                                            journey.nbTransfers(),
                                            journey.departureDatetime(),
                                            journey.arrivalDatetime(),
                                            journey.co2Emission() != null
                                                    ? journey.co2Emission().value()
                                                    : 0,
                                            journey.co2Emission() != null
                                                    ? journey.co2Emission().unit()
                                                    : null,
                                            sectionDtos);
                                })
                        .toList();

        return new SncfJourneyResponse(dtos);
    }

    private static SncfJourneySectionDto mapSection(Section section) {
        DisplayInformations info = section.displayInformations();
        return new SncfJourneySectionDto(
                section.type(),
                section.duration(),
                section.departureDatetime(),
                section.arrivalDatetime(),
                section.from() != null ? section.from().name() : null,
                section.to() != null ? section.to().name() : null,
                info != null ? info.commercialMode() : null,
                info != null ? info.network() : null,
                info != null ? info.label() : null,
                info != null ? info.code() : null,
                info != null ? info.color() : null,
                info != null ? info.direction() : null);
    }
}
