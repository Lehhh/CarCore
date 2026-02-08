package br.com.fiap.soat7.infra;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserInitializerTest {

    @Test
    void runShouldDoNothingWhenAdminAlreadyExists() {
        AppUserRepository repo = mock(AppUserRepository.class);
        PasswordEncoder pe = mock(PasswordEncoder.class);
        AdminUserInitializer init = new AdminUserInitializer(repo, pe);

        setField(init, "email", "ADMIN@LOCAL");

        when(repo.existsByEmail("admin@local")).thenReturn(true);

        init.run();

        verify(repo).existsByEmail("admin@local");
        verifyNoMoreInteractions(repo, pe);
    }

    @Test
    void runShouldCreateAdminWhenMissingAndNormalizeCpf() {
        AppUserRepository repo = mock(AppUserRepository.class);
        PasswordEncoder pe = mock(PasswordEncoder.class);
        AdminUserInitializer init = new AdminUserInitializer(repo, pe);

        setField(init, "name", "Admin");
        setField(init, "email", "ADMIN@LOCAL");
        setField(init, "cpf", "123.456.789-01");
        setField(init, "password", "secret");

        when(repo.existsByEmail("admin@local")).thenReturn(false);
        when(pe.encode("secret")).thenReturn("HASH");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        when(repo.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        init.run();

        AppUser saved = captor.getValue();
        assertEquals("Admin", saved.getName());
        assertEquals("admin@local", saved.getEmail());
        assertEquals("12345678901", saved.getCpf());
        assertEquals("HASH", saved.getPasswordHash());
        assertEquals(RoleUser.ROLE_ADMIN, saved.getRole());

        verify(repo).existsByEmail("admin@local");
        verify(pe).encode("secret");
        verify(repo).save(any(AppUser.class));
        verifyNoMoreInteractions(repo, pe);
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
