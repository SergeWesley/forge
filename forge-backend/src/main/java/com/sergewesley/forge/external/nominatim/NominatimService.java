package com.sergewesley.forge.external.nominatim;

import com.sergewesley.forge.dto.nominatim.NominatimAddressResponse;
import com.sergewesley.forge.dto.nominatim.NominatimResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class NominatimService extends BaseExternalService {

    public NominatimService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<NominatimAddressResponse> geocodeAddress(String address) {
        String url =
                UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .queryParam("q", address)
                        .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "ForgeBackend-App/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return executeExchangeCall(
                url,
                HttpMethod.GET,
                entity,
                NominatimResponse[].class,
                NominatimResponse::toAddressResponse,
                "Recherche de géocodage via Nominatim pour : " + address,
                "Erreur lors de la recherche de géocodage sur Nominatim",
                log);
    }
}
