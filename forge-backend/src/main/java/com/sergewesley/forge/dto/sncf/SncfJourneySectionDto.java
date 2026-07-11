package com.sergewesley.forge.dto.sncf;

/** Représentation d'une section (étape) d'un trajet : marche, train, correspondance, etc. */
public record SncfJourneySectionDto(
        String type,
        int duration,
        String departureTime,
        String arrivalTime,
        String fromName,
        String toName,
        String commercialMode,
        String network,
        String label,
        String code,
        String color,
        String direction) {}
