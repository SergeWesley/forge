package com.sergewesley.forge.external.mcu;

import com.sergewesley.forge.dto.mcu.McuCountdownResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class McuService extends BaseExternalService {

    private static final String API_URL = "https://www.whenisthenextmcufilm.com/api";

    public McuService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<McuCountdownResponse> getNextMcuFilm() {
        return executeGetCall(
                API_URL,
                McuCountdownResponse.class,
                Function.identity(),
                "Récupération de la prochaine production MCU",
                "Erreur lors de la récupération du compte à rebours MCU",
                log);
    }
}
