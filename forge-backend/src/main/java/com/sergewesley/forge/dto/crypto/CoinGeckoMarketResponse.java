package com.sergewesley.forge.dto.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoinGeckoMarketResponse(
        String id,
        String symbol,
        String name,
        String image,
        @JsonProperty("current_price") Double currentPrice,
        @JsonProperty("market_cap") Double marketCap,
        @JsonProperty("price_change_percentage_24h") Double priceChangePercentage24h,
        @JsonProperty("high_24h") Double high24h,
        @JsonProperty("low_24h") Double low24h) {
    public static CoinGeckoMarketResponse getFirstItem(CoinGeckoMarketResponse[] items) {
        return (items != null && items.length > 0) ? items[0] : null;
    }
}
