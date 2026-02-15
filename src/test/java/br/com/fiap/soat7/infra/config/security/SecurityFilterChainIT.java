package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterChainIT {

    private final SecurityConfig config = new SecurityConfig();

    // -------------------------
    // passwordEncoder()
    // -------------------------

    @Test
    void passwordEncoder_deveSerBCrypt_eFuncionar() {
        PasswordEncoder encoder = config.passwordEncoder();

        String hash = encoder.encode("senha");
        assertNotNull(hash);
        assertTrue(encoder.matches("senha", hash));
        assertFalse(encoder.matches("senha-errada", hash));
    }

    // -------------------------
    // authenticationManager()
    // -------------------------

    @Test
    void authenticationManager_deveDelegarParaAuthenticationConfiguration() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);

        when(authConfig.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager actual = config.authenticationManager(authConfig);

        assertSame(manager, actual);
        verify(authConfig).getAuthenticationManager();
        verifyNoMoreInteractions(authConfig);
    }

    // -------------------------
    // jwtAuthConverter()
    // -------------------------

    @Test
    void jwtAuthConverter_quandoRolesLista_deveNormalizarERetornarAuthorities() {
        Converter<Jwt, JwtAuthenticationToken> converter = config.jwtAuthConverter();

        Jwt jwt = jwtWithClaims(
                "sub", "user-1",
                Map.of("roles", List.of("ROLE_ADMIN", " USER ", "", "  "))
        );

        JwtAuthenticationToken token = converter.convert(jwt);
        assertNotNull(token);

        assertEquals("user-1", token.getName()); // subject
        var authorities = token.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        // "ROLE_ADMIN" fica, " USER " vira "ROLE_USER", blanks são filtrados
        assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"), authorities);
    }

    @Test
    void jwtAuthConverter_quandoRolesNulaUsaRoleString() {
        Converter<Jwt, JwtAuthenticationToken> converter = config.jwtAuthConverter();

        Jwt jwt = jwtWithClaims(
                "sub", "user-2",
                Map.of("role", "admin")
        );

        JwtAuthenticationToken token = converter.convert(jwt);
        assertNotNull(token);

        var authorities = token.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        assertEquals(List.of("ROLE_admin"), authorities); // seu código NÃO uppercasa; só prefixa
    }

    @Test
    void jwtAuthConverter_quandoRoleBlank_deveVirSemAuthorities() {
        Converter<Jwt, JwtAuthenticationToken> converter = config.jwtAuthConverter();

        Jwt jwt = jwtWithClaims(
                "sub", "user-3",
                Map.of("role", "   ")
        );

        JwtAuthenticationToken token = converter.convert(jwt);
        assertNotNull(token);

        assertTrue(token.getAuthorities().isEmpty());
    }

    // -------------------------
    // filterChain(HttpSecurity)
    // -------------------------



//    @Test
//    void filterChain_deveConstruirComSucesso() throws Exception {
//
//        // 1) ObjectPostProcessor fake (padrão em testes unitários)
//        ObjectPostProcessor<Object> opp = new ObjectPostProcessor<>() {
//            @Override
//            public <T> T postProcess(T object) {
//                return object;
//            }
//        };
//
//        // 2) AuthenticationManagerBuilder real (não pode ser null)
//        AuthenticationManagerBuilder authBuilder = new AuthenticationManagerBuilder(opp);
//
//        // 3) HttpSecurity real
//        HttpSecurity http = new HttpSecurity(opp, authBuilder, Map.of());
//
//        // 4) Para Resource Server (jwt), registramos um JwtDecoder fake para não depender de configuração externa
//        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
//        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
//
//        // registra provider no authBuilder (assim o http consegue montar o AuthenticationManager)
//        authBuilder.authenticationProvider(provider);
//
//        // 5) Executa o seu bean
//        SecurityFilterChain chain = config.filterChain(http);
//
//        assertNotNull(chain);
//        assertInstanceOf(DefaultSecurityFilterChain.class, chain);
//
//        // 6) (Opcional) sanity-check: garantir que tem filtro de bearer token no chain,
//        // indicando que oauth2ResourceServer(jwt) entrou mesmo.
//        DefaultSecurityFilterChain dfc = (DefaultSecurityFilterChain) chain;
//        boolean hasBearerFilter = dfc.getFilters().stream().anyMatch(f -> f instanceof BearerTokenAuthenticationFilter);
//        assertTrue(hasBearerFilter, "Esperava BearerTokenAuthenticationFilter no SecurityFilterChain");
//    }

    // -------------------------
    // Helpers
    // -------------------------

    private static Jwt jwtWithClaims(String subjectKey, String subjectValue, Map<String, Object> claims) {
        // subject é usado via jwt.getSubject(). Em Jwt do Spring, subject vem do claim "sub".
        // Então garantimos "sub" aqui.
        Map<String, Object> fullClaims = new java.util.HashMap<>(claims);
        fullClaims.putIfAbsent("sub", subjectValue);

        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                fullClaims
        );
    }
}
