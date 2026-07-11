package com.sergewesley.forge.dto.sncf;

import java.util.List;

/** Réponse propre renvoyée à notre client, contenant la liste des itinéraires possibles. */
public record SncfJourneyResponse(List<SncfJourneyDto> journeys) {}
