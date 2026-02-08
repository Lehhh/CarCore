package br.com.fiap.soat7.infra.config.api;

import br.com.fiap.soat7.data.dto.car.CarSyncRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CarStoreViewClient {

    private final WebClient carStoreViewWebClient;

    public CarStoreViewClient(WebClient carStoreViewWebClient) {
        this.carStoreViewWebClient = carStoreViewWebClient;
    }

    /**
     * Dispara o upsert no serviço View.
     * Fake simples: sem retry, sem auth de serviço.
     */
    public void upsertCar(CarSyncRequest req) {
        carStoreViewWebClient.post()
                .uri("/sync/cars")
                .bodyValue(req)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> System.err.println("Falha ao sincronizar carro no View: " + e.getMessage()))
                .subscribe(); // fake async
    }
}
