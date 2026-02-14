package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrentTokenProviderTest {

    private final CurrentTokenProvider provider = new CurrentTokenProvider();

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnTokenWhenAuthenticationIsJwt() {
        Jwt jwt = new Jwt(
                "token-123",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of("sub", "user")
        );

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        assertEquals("token-123", provider.getToken());
    }

    @Test
    void shouldReturnNullWhenAuthenticationIsNotJwt() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertNull(provider.getToken());
    }
}
