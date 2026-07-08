package com.sergewesley.forge.external;

import com.sergewesley.forge.exception.ExternalServiceException;
import com.sergewesley.forge.exception.ExternalServiceRateLimitException;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        // Factorisation : On délègue à la méthode principale (sans header spécifique)
        return executeExchangeCall(
                url,
                HttpMethod.GET,
                null,
                responseType,
                mapper,
                logInfoMessage,
                logErrorMessage,
                logger);
    }

    /**
     * Exécute un appel HTTP via exchange (utile pour passer des headers spécifiques) et gère le
     * parsing.
     *
     * @param url L'URL de l'API à appeler
     * @param method La méthode HTTP
     * @param requestEntity L'entité de la requête contenant les headers/body
     * @param responseType La classe du DTO de réponse attendu
     * @param mapper Fonction pour extraire l'objet final depuis le DTO de réponse
     * @param logInfoMessage Message de log en cas de succès (INFO)
     * @param logErrorMessage Message de log en cas d'erreur (ERROR)
     * @param logger Le logger de la classe enfant
     * @param <T> Type du DTO de réponse
     * @param <R> Type de la donnée retournée
     * @return Un Optional contenant la donnée extraite, ou vide en cas d'erreur
     */
    protected <T, R> Optional<R> executeExchangeCall(
            String url,
            HttpMethod method,
            HttpEntity<?> requestEntity,
            Class<T> responseType,
            Function<T, R> mapper,
            String logInfoMessage,
            String logErrorMessage,
            Logger logger) {
        try {
            logger.info(logInfoMessage);
            ResponseEntity<T> responseEntity =
                    restTemplate.exchange(url, method, requestEntity, responseType);
            T response = responseEntity.getBody();
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
                return Optional.empty();
            }
            throw new ExternalServiceException(logErrorMessage + " : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(logErrorMessage, e);
            throw new ExternalServiceException(logErrorMessage + " : " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
