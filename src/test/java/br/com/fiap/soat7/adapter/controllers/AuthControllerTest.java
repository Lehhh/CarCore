package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.infra.config.security.JwtIssuer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    JwtIssuer jwtIssuer;

    @InjectMocks
    AuthController controller;

    @Test
    void loginShouldReturnAccessTokenAndUseProvidedEmail() throws Exception {
        String email = "test@local";
        String token = "token-123";

        when(jwtIssuer.issue(eq(email), eq(email), eq(List.of("USER")), eq(3600L)))
                .thenReturn(token);

        ResponseEntity<Map<String, String>> response = controller.login(Map.of("email", email));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().get("accessToken"));

        verify(jwtIssuer).issue(eq(email), eq(email), eq(List.of("USER")), eq(3600L));
        verifyNoMoreInteractions(jwtIssuer);
    }

    @Test
    void loginShouldDefaultEmailWhenMissing() throws Exception {
        String email = "user@local";
        String token = "token-default";

        when(jwtIssuer.issue(eq(email), eq(email), eq(List.of("USER")), eq(3600L)))
                .thenReturn(token);

        ResponseEntity<Map<String, String>> response = controller.login(Map.of());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(token, response.getBody().get("accessToken"));
        verify(jwtIssuer).issue(eq(email), eq(email), eq(List.of("USER")), eq(3600L));
        verifyNoMoreInteractions(jwtIssuer);
    }
}
