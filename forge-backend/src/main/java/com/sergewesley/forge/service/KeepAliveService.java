package com.sergewesley.forge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.public.url:https://forge-v1ui.onrender.com}")
    private String appPublicUrl;

    // @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void pingSelf() {
        String healthUrl = appPublicUrl + "/health";
        try {
            restTemplate.getForObject(healthUrl, String.class);
            logger.info("Self-ping successful: {}", healthUrl);
        } catch (Exception e) {
            logger.error("Self-ping failed for URL: {}", healthUrl, e);
        }
    }
}
