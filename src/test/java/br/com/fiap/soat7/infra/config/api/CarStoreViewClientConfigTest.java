package br.com.fiap.soat7.infra.config.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class CarStoreViewClientConfigTest {

    @Test
    void carStoreViewWebClientShouldBeCreated() {
        CarStoreViewClientConfig cfg = new CarStoreViewClientConfig();
        WebClient client = cfg.carStoreViewWebClient("http://localhost:8080");
        assertNotNull(client);
    }
}
