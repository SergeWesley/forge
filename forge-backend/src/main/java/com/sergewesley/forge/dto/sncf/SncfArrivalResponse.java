package com.sergewesley.forge.dto.sncf;

import java.util.List;

/** Réponse propre renvoyée à notre client, contenant la liste des prochaines arrivées. */
public record SncfArrivalResponse(List<SncfArrivalDto> arrivals) {}
