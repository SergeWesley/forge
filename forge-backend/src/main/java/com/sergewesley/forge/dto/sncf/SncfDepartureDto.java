package com.sergewesley.forge.dto.sncf;

/**
 * Représentation d'un seul départ, simplifié pour notre client.
 *
 * @param direction La destination du train (ex: "Paris Gare de Lyon")
 * @param commercialMode Le mode commercial (ex: "TGV", "TER", "Intercités")
 * @param network Le réseau (ex: "SNCF")
 * @param label Le label de la ligne
 * @param code Le code de la ligne
 * @param color La couleur hex de la ligne (ex: "000000")
 * @param departureTime L'heure de départ prévue (format Navitia: YYYYMMDDTHHmmss)
 * @param baseDepartureTime L'heure de départ théorique (avant perturbations)
 */
public record SncfDepartureDto(
        String direction,
        String commercialMode,
        String network,
        String label,
        String code,
        String color,
        String departureTime,
        String baseDepartureTime) {}
