package com.sergewesley.forge.exception;

public class ExternalServiceRateLimitException extends ExternalServiceException {
    public ExternalServiceRateLimitException(String message) {
        super(message);
    }
}
