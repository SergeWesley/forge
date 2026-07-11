package com.sergewesley.forge.dto.sncf;

import java.util.List;

/**
 * Représentation d'un trajet complet avec ses sections, durée, correspondances et empreinte CO2.
 */
public record SncfJourneyDto(
        int duration,
        int nbTransfers,
        String departureTime,
        String arrivalTime,
        double co2EmissionValue,
        String co2EmissionUnit,
        List<SncfJourneySectionDto> sections) {}
