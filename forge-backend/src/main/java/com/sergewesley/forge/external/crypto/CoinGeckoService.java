package com.sergewesley.forge.external.crypto;

import com.sergewesley.forge.dto.crypto.CoinGeckoMarketResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CoinGeckoService extends BaseExternalService {

    private static final Logger log = LoggerFactory.getLogger(CoinGeckoService.class);
    private static final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3";

    public CoinGeckoService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<CoinGeckoMarketResponse> getMarketData(String coinId, String currency) {
        String url =
                UriComponentsBuilder.fromUriString(COINGECKO_BASE_URL)
                        .path("/coins/markets")
                        .queryParam("vs_currency", currency)
                        .queryParam("ids", coinId)
                        .toUriString();

        return executeGetCall(
                url,
                CoinGeckoMarketResponse[].class,
                CoinGeckoMarketResponse::getFirstItem,
                "Récupération des données CoinGecko pour la monnaie : " + coinId,
                "Erreur lors de la récupération des données CoinGecko",
                log);
    }
}
