package br.com.fiap.soat7.infra.config.security;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserDetailsServiceTest {

    @Test
    void loadUserByUsernameShouldMapDomainUserToSpringUserDetails() {
        AppUserRepository repo = mock(AppUserRepository.class);
        AppUserDetailsService service = new AppUserDetailsService(repo);

        AppUser u = new AppUser("A", "a@local", "12345678901", "HASH", RoleUser.ROLE_ADMIN);
        when(repo.findByEmail("a@local")).thenReturn(Optional.of(u));

        var details = service.loadUserByUsername("A@LOCAL");

        assertEquals("a@local", details.getUsername());
        assertEquals("HASH", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(repo).findByEmail("a@local");
    }

    @Test
    void loadUserByUsernameShouldThrowWhenMissing() {
        AppUserRepository repo = mock(AppUserRepository.class);
        AppUserDetailsService service = new AppUserDetailsService(repo);
        when(repo.findByEmail("x@local")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("X@LOCAL"));

        verify(repo).findByEmail("x@local");
    }
}
