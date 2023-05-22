package com.eBankingApp.jabak_lah_backend.vonageConfig;

import com.vonage.client.VonageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VonageConfig {
    @Value("${vonage.apiKey}")
    private String apiKey;

    @Value("${vonage.apiSecret}")
    private String apiSecret;
    @Bean
    public VonageClient vonageClient() {
        return VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();
    }
}
