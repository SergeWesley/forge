package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.crypto.CoinGeckoMarketResponse;
import com.sergewesley.forge.exception.ResourceNotFoundException;
import com.sergewesley.forge.external.crypto.CoinGeckoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
@Tag(name = "Crypto", description = "api.crypto.tag.description")
public class CryptoController {

    private final CoinGeckoService coinGeckoService;
    private final MessageSource messageSource;

    public CryptoController(CoinGeckoService coinGeckoService, MessageSource messageSource) {
        this.coinGeckoService = coinGeckoService;
        this.messageSource = messageSource;
    }

    @GetMapping("/market")
    @Operation(
            summary = "api.crypto.market.summary",
            description = "api.crypto.market.desc",
            tags = {"Generative UI"},
            extensions = {
                @Extension(
                        name = "x-generative-ui",
                        properties = {@ExtensionProperty(name = "enabled", value = "true")})
            })
    public CoinGeckoMarketResponse getMarketData(
            @Parameter(description = "Coin ID (e.g. bitcoin, ethereum)", required = true)
                    @RequestParam
                    String coin,
            @Parameter(description = "Currency (e.g. usd, eur)", required = false)
                    @RequestParam(defaultValue = "usd")
                    String currency) {

        return coinGeckoService
                .getMarketData(coin, currency)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        messageSource.getMessage(
                                                "error.crypto.unavailable",
                                                new Object[] {coin},
                                                LocaleContextHolder.getLocale())));
    }
}
