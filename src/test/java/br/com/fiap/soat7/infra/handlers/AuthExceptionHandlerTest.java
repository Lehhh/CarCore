package br.com.fiap.soat7.infra.handlers;

import br.com.fiap.soat7.usecase.services.exceptions.AuthExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthExceptionHandlerTest {

    private final AuthExceptionHandler handler = new AuthExceptionHandler();

    @Test
    void shouldReturn401AndMessageWhenIllegalArgumentExceptionIsThrown() {
        // given
        String errorMessage = "Token inválido";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        // when
        ResponseEntity<Map<String, String>> response = handler.handle(ex);

        // then
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void shouldReturnDefaultMessageWhenNull() {
        IllegalArgumentException ex = new IllegalArgumentException(); // message = null

        ResponseEntity<Map<String, String>> response = handler.handle(ex);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized", response.getBody().get("message"));
    }
}
