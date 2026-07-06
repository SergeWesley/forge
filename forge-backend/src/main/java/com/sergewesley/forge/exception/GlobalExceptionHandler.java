package com.sergewesley.forge.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(
                messageSource.getMessage(
                        "error.title.notfound", null, LocaleContextHolder.getLocale()));
        return problemDetail;
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalServiceException(ExternalServiceException ex) {
        // Renvoie un 503 (Service Unavailable) car le service externe est indisponible
        String detailMessage =
                messageSource.getMessage(
                        "error.detail.externalservice", null, LocaleContextHolder.getLocale());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, detailMessage);
        problemDetail.setTitle(
                messageSource.getMessage(
                        "error.title.externalservice", null, LocaleContextHolder.getLocale()));
        return problemDetail;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
        String detailMessage =
                messageSource.getMessage(
                        "error.route.notfound",
                        new Object[] {ex.getResourcePath()},
                        LocaleContextHolder.getLocale());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detailMessage);
        problemDetail.setTitle(
                messageSource.getMessage(
                        "error.title.notfound", null, LocaleContextHolder.getLocale()));
        return problemDetail;
    }

    @ExceptionHandler(ExternalServiceRateLimitException.class)
    public ProblemDetail handleExternalServiceRateLimitException(
            ExternalServiceRateLimitException ex) {
        String detailMessage =
                messageSource.getMessage(
                        "error.detail.ratelimit",
                        null,
                        ex.getMessage(),
                        LocaleContextHolder.getLocale());
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, detailMessage);
        problemDetail.setTitle(
                messageSource.getMessage(
                        "error.title.ratelimit", null, LocaleContextHolder.getLocale()));
        return problemDetail;
    }
}
