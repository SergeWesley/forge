package com.sergewesley.forge.dto.sncf;

import java.util.List;

/**
 * Réponse propre renvoyée à notre client, contenant la liste des prochains départs.
 *
 * @param departures Liste des prochains départs pour la gare demandée
 */
public record SncfDepartureResponse(List<SncfDepartureDto> departures) {}
