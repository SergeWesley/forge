package com.sergewesley.forge.dto.sncf;

/** Représentation d'une seule arrivée, simplifiée pour notre client. */
public record SncfArrivalDto(
        String direction,
        String commercialMode,
        String network,
        String label,
        String code,
        String color,
        String arrivalTime,
        String baseArrivalTime) {}
