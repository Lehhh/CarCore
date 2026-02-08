package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void filterChainShouldBuildWithoutThrowing() throws Exception {
        SecurityConfig cfg = new SecurityConfig();

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        SecurityFilterChain expected = mock(SecurityFilterChain.class);

        doReturn(http).when(http).csrf(any());
        doReturn(http).when(http).sessionManagement(any());
        doReturn(http).when(http).authorizeHttpRequests(any());
        doReturn(http).when(http).httpBasic(any());
        doReturn(expected).when(http).build();

        SecurityFilterChain chain = cfg.filterChain(http);

        assertSame(expected, chain);

        verify(http).csrf(any());
        verify(http).sessionManagement(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).httpBasic(any());
        verify(http).build();
    }


    @Test
    void passwordEncoderShouldReturnBCrypt() {
        SecurityConfig cfg = new SecurityConfig();
        assertNotNull(cfg.passwordEncoder());
        assertTrue(cfg.passwordEncoder().getClass().getSimpleName().contains("BCrypt"));
    }

    @Test
    void authenticationManagerShouldDelegateToAuthConfiguration() throws Exception {
        SecurityConfig cfg = new SecurityConfig();
        AuthenticationConfiguration ac = mock(AuthenticationConfiguration.class);
        AuthenticationManager am = mock(AuthenticationManager.class);
        when(ac.getAuthenticationManager()).thenReturn(am);

        AuthenticationManager result = cfg.authenticationManager(ac);

        assertSame(am, result);
        verify(ac).getAuthenticationManager();
    }
}
