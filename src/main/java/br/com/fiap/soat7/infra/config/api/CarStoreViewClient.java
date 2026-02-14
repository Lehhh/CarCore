package br.com.fiap.soat7.infra.config.api;

import br.com.fiap.soat7.data.dto.car.CarSyncRequest;
import br.com.fiap.soat7.infra.config.security.CurrentTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarStoreViewClient {

    private final CurrentTokenProvider tokenProvider;

    @Value("${CARSTORE_VIEW_BASE_URL}")
    private String baseUrl;

    public void upsertCar(CarSyncRequest req) {
        String token = tokenProvider.getToken();
        System.out.println(baseUrl);

        WebClient.builder()
                .baseUrl(baseUrl).build().post()
                .uri("/sync/cars")
                .headers(h -> { if (token != null && !token.isBlank()) h.setBearerAuth(token); })
                .bodyValue(req)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> System.err.println("Falha ao sincronizar carro no View: " + e.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }
}
