package br.com.fiap.soat7.infra.config.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class CarViewSyncClient {

    @Value("${CARLISTVIEW}")
    private String carListViewUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(carListViewUrl).build();
    }
}
