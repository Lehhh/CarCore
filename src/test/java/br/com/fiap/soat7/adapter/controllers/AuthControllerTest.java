package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import br.com.fiap.soat7.data.dto.user.LoginRequest;
import br.com.fiap.soat7.infra.config.security.JwtIssuer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    JwtIssuer jwtIssuer;
    @Mock
    AppUserRepository userRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthController controller;

    @Test
    void loginShouldReturnAccessTokenWhenCredentialsAreValid() throws Exception {
        String email = "admin@local";
        String rawPass = "admin123";
        String hash = "$2a$10$hash";
        RoleUser roles = RoleUser.ROLE_ADMIN;
        String token = "token-123";

        AppUser user = mock(AppUser.class);
        when(user.getEmail()).thenReturn(email);
        when(user.getPasswordHash()).thenReturn(hash);
        when(user.getRole()).thenReturn(roles);

        when(userRepo.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, hash)).thenReturn(true);
        when(jwtIssuer.issue(email, email, roles, 3600L)).thenReturn(token);

        ResponseEntity<Map<String, String>> response =
                controller.login(new LoginRequest(email, rawPass));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().get("accessToken"));

        verify(userRepo).findByEmailIgnoreCase(email);
        verify(passwordEncoder).matches(rawPass, hash);
        verify(jwtIssuer).issue(email, email, roles, 3600L);
        verifyNoMoreInteractions(userRepo, passwordEncoder, jwtIssuer);
    }

    @Test
    void loginShouldThrowWhenUserNotFound() {
        String email = "missing@local";
        String rawPass = "any";

        when(userRepo.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.login(new LoginRequest(email, rawPass))
        );

        assertEquals("Credenciais inválidas", ex.getMessage());

        verify(userRepo).findByEmailIgnoreCase(email);
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(passwordEncoder, jwtIssuer);
    }

    @Test
    void loginShouldThrowWhenPasswordIsInvalid() {
        String email = "user@local";
        String rawPass = "wrong";
        String hash = "$2a$10$hash";

        AppUser user = mock(AppUser.class);
        when(user.getPasswordHash()).thenReturn(hash);

        when(userRepo.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, hash)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.login(new LoginRequest(email, rawPass))
        );

        assertEquals("Credenciais inválidas", ex.getMessage());

        verify(userRepo).findByEmailIgnoreCase(email);
        verify(passwordEncoder).matches(rawPass, hash);
        verifyNoMoreInteractions(userRepo, passwordEncoder);
        verifyNoInteractions(jwtIssuer);
    }
}
