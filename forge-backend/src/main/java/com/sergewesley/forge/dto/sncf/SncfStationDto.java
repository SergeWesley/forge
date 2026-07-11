package com.sergewesley.forge.dto.sncf;

/** Représentation simplifiée d'une gare/station SNCF renvoyée à notre client. */
public record SncfStationDto(String id, String name, String label) {}
