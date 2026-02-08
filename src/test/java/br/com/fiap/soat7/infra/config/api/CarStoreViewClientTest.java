package br.com.fiap.soat7.infra.config.api;

import br.com.fiap.soat7.data.dto.car.CarSyncRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class CarStoreViewClientTest {

    @Test
    void upsertCarShouldInvokeWebClientChain() {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec uriSpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(postSpec).when(webClient).post();
        doReturn(uriSpec).when(postSpec).uri("/sync/cars");
        doReturn(headersSpec).when(uriSpec).bodyValue(any());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.just(ResponseEntity.noContent().build()))
                .when(responseSpec).toBodilessEntity();

        CarStoreViewClient client = new CarStoreViewClient(webClient);

        client.upsertCar(new CarSyncRequest(
                1L, "A", "B", 2000, "C",
                new BigDecimal("1.00"),
                Instant.now()
        ));

        verify(webClient).post();
        verify(postSpec).uri("/sync/cars");
        verify(uriSpec).bodyValue(any(CarSyncRequest.class));
        verify(headersSpec).retrieve();
        verify(responseSpec).toBodilessEntity();
        verifyNoMoreInteractions(webClient, postSpec, uriSpec, headersSpec, responseSpec);
    }

    @Test
    void upsertCarShouldNotThrowOnErrorPublisher() {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec uriSpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(postSpec).when(webClient).post();
        doReturn(uriSpec).when(postSpec).uri("/sync/cars");
        doReturn(headersSpec).when(uriSpec).bodyValue(any());
        doReturn(responseSpec).when(headersSpec).retrieve();
        doReturn(Mono.error(new RuntimeException("boom")))
                .when(responseSpec).toBodilessEntity();

        CarStoreViewClient client = new CarStoreViewClient(webClient);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                client.upsertCar(new CarSyncRequest(
                        1L, "A", "B", 2000, "C",
                        new BigDecimal("1.00"),
                        Instant.now()
                ))
        );

        verify(responseSpec).toBodilessEntity();
    }


}
