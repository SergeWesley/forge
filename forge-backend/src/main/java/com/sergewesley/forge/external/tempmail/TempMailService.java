package com.sergewesley.forge.external.tempmail;

import com.sergewesley.forge.dto.tempmail.TempEmailResponse;
import com.sergewesley.forge.dto.tempmail.TempMailListResponse;
import com.sergewesley.forge.external.BaseExternalService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TempMailService extends BaseExternalService {

    public TempMailService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<TempEmailResponse> generateTempEmail() {
        String url = "https://api.guerrillamail.com/ajax.php?f=get_email_address";
        return executeGetCall(
                url,
                TempEmailResponse.class,
                response -> response,
                "Génération d'une adresse e-mail temporaire (Guerrilla Mail)",
                "Erreur lors de la génération de l'adresse e-mail",
                log);
    }

    public Optional<TempMailListResponse> checkEmails(String sidToken) {
        String url =
                "https://api.guerrillamail.com/ajax.php?f=get_email_list&offset=0&sid_token=" + sidToken;
        return executeGetCall(
                url,
                TempMailListResponse.class,
                response -> response,
                "Vérification des e-mails pour le token : " + sidToken,
                "Erreur lors de la vérification des e-mails",
                log);
    }
}
