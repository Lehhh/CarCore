package br.com.fiap.soat7.infra.config.api;

import br.com.fiap.soat7.data.dto.car.CarSyncRequest;
import br.com.fiap.soat7.infra.config.security.CurrentTokenProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarStoreViewClientTest {

    @Mock
    private CurrentTokenProvider tokenProvider;
    @Mock
    private WebClient.Builder builder;
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;
    @Mock
    private WebClient.RequestBodySpec bodySpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private CarStoreViewClient client;

    private MockedStatic<WebClient> webClientStatic; // <-- guardar para fechar

    @BeforeEach
    void setup() {
        client = new CarStoreViewClient(tokenProvider);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);

        // injeta baseUrl no @Value
        ReflectionTestUtils.setField(client, "baseUrl", "http://carstore_view_app:8081");

        // mock do builder estático
        webClientStatic = mockStatic(WebClient.class);
        webClientStatic.when(WebClient::builder).thenReturn(builder);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);

        // chain correta
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("/sync/cars"))).thenReturn(bodySpec);

        when(bodySpec.headers(any())).thenReturn(bodySpec);

        // bodyValue retorna RequestHeadersSpec<?> (não RequestBodySpec)
        when(bodySpec.bodyValue(any(CarSyncRequest.class))).thenReturn(headersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
    }

    @AfterEach
    void tearDown() {
        webClientStatic.close(); // <-- evita "static mocking already registered"
    }

    @Test
    void shouldSendBearerTokenWhenTokenExists() {
        when(tokenProvider.getToken()).thenReturn("token-123");

        client.upsertCar(mock(CarSyncRequest.class));

        verify(tokenProvider).getToken();
        verify(builder).baseUrl("http://carstore_view_app:8081");
        verify(uriSpec).uri("/sync/cars");
        verify(bodySpec).headers(any());
        verify(bodySpec).bodyValue(any(CarSyncRequest.class));
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void shouldCallWithoutBearerWhenTokenIsNull() {
        when(tokenProvider.getToken()).thenReturn(null);

        client.upsertCar(mock(CarSyncRequest.class));

        verify(tokenProvider).getToken();
        verify(bodySpec).headers(any());
        verify(responseSpec).toBodilessEntity();
    }
}
