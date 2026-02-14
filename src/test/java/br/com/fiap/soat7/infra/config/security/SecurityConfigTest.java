package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private SecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new SecurityConfig();
    }

    @Test
    void passwordEncoder_shouldReturnBCrypt() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void authenticationManager_shouldDelegateToAuthenticationConfiguration() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);

        when(authConfig.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = config.authenticationManager(authConfig);

        assertSame(manager, result);
        verify(authConfig).getAuthenticationManager();
        verifyNoMoreInteractions(authConfig);
    }

    @Test
    void jwtAuthConverter_shouldUseRolesListClaim_andNormalizePrefix() {
        Jwt jwt = jwtWithClaims(
                "sub", "user-1",
                Map.of("roles", List.of("ADMIN", " ROLE_USER ", "ROLE_MANAGER"))
        );

        JwtAuthenticationToken token = config.jwtAuthConverter().convert(jwt);

        assertNotNull(token);
        assertEquals("user-1", token.getName());

        List<String> authorities = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        assertEquals(List.of("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER"), authorities);
    }

    @Test
    void jwtAuthConverter_shouldFallbackToSingleRoleClaim_whenRolesMissing() {
        Jwt jwt = jwtWithClaims(
                "sub", "user-2",
                Map.of("role", "ADMIN")
        );

        JwtAuthenticationToken token = config.jwtAuthConverter().convert(jwt);

        assertNotNull(token);

        List<String> authorities = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(List.of("ROLE_ADMIN"), authorities);
    }

    @Test
    void jwtAuthConverter_shouldReturnEmptyAuthorities_whenNoRoleClaims() {
        Jwt jwt = jwtWithClaims("sub", "user-3", Map.of());

        JwtAuthenticationToken token = config.jwtAuthConverter().convert(jwt);

        assertNotNull(token);
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void jwtAuthConverter_shouldIgnoreBlankRoles_andTrim() {
        Jwt jwt = jwtWithClaims(
                "sub", "user-4",
                Map.of("roles", List.of("  ", "", " USER  ", "ROLE_ADMIN"))
        );

        JwtAuthenticationToken token = config.jwtAuthConverter().convert(jwt);

        List<String> authorities = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"), authorities);
    }

    /**
     * Helper para criar Jwt real (sem mock), preenchendo claims mínimos.
     */
    private static Jwt jwtWithClaims(String subjectKey, String subject, Map<String, Object> claims) {
        // Jwt.Builder existe no Spring Security (org.springframework.security.oauth2.jwt.Jwt)
        // headers pode ficar vazio
        return Jwt.withTokenValue("fake-token")
                .header("alg", "none")
                .claim(subjectKey, subject) // geralmente "sub"
                .claims(c -> c.putAll(claims))
                .build();
    }
}
