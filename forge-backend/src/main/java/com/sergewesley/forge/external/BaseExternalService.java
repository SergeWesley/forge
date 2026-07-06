package com.sergewesley.forge.external;

import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.exception.ExternalServiceRateLimitException;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public abstract class BaseExternalService {

    protected final RestTemplate restTemplate;

    protected BaseExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Exécute un appel GET et gère le parsing, les logs et les erreurs de manière générique.
     *
     * @param url L'URL de l'API à appeler
     * @param responseType La classe du DTO de réponse attendu
     * @param mapper Fonction pour extraire l'objet final depuis le DTO de réponse
     * @param logInfoMessage Message de log en cas de succès (INFO)
     * @param logErrorMessage Message de log en cas d'erreur (ERROR)
     * @param logger Le logger de la classe enfant (pour garder le bon contexte)
     * @param <T> Type du DTO de réponse
     * @param <R> Type de la donnée retournée
     * @return Un Optional contenant la donnée extraite, ou vide en cas d'erreur
     */
    protected <T, R> Optional<R> executeGetCall(
            String url,
            Class<T> responseType,
            Function<T, R> mapper,
            String logInfoMessage,
            String logErrorMessage,
            Logger logger) {
        try {
            logger.info(logInfoMessage);
            T response = restTemplate.getForObject(url, responseType);
            if (response != null) {
                R mappedData = mapper.apply(response);
                if (mappedData != null) {
                    return Optional.of(mappedData);
                }
            }
        } catch (RestClientResponseException e) {
            logger.error(
                    "{} - Status: {}, Response: {}",
                    logErrorMessage,
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            if (e.getStatusCode().value() == 429) {
                throw new ExternalServiceRateLimitException("error.detail.ratelimit");
            }
            if (e.getStatusCode().value() == 404) {
                // Pour les ressources non trouvées, on renvoie un Optional vide
                // afin que le contrôleur gère son propre ResourceNotFoundException
                return Optional.empty();
            }
            // Fail-fast sur les autres erreurs HTTP (5xx, 4xx non gérés)
            throw new ExternalServiceException(logErrorMessage + " : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(logErrorMessage, e);
            throw new ExternalServiceException(logErrorMessage + " : " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
