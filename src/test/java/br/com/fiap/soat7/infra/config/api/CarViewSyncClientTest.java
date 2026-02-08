package br.com.fiap.soat7.infra.config.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarViewSyncClientTest {

    @Test
    void webClientShouldUseBuilderWithBaseUrlFromField() {
        CarViewSyncClient cfg = new CarViewSyncClient();

        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient expected = mock(WebClient.class);

        setField(cfg, "carListViewUrl", "http://view.local");
        when(builder.baseUrl("http://view.local")).thenReturn(builder);
        when(builder.build()).thenReturn(expected);

        WebClient result = cfg.webClient(builder);

        assertSame(expected, result);
        verify(builder).baseUrl("http://view.local");
        verify(builder).build();
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
