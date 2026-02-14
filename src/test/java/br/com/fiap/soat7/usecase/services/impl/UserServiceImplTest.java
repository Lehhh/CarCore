package br.com.fiap.soat7.usecase.services.impl;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import br.com.fiap.soat7.data.dto.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    AppUserRepository repo;

    @Mock
    PasswordEncoder passwordEncoder;

    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(repo, passwordEncoder);
    }

    @Test
    void createShouldNormalizeAndSaveUser() {
        UserCreateRequest req = new UserCreateRequest(
                "  Leandro ",
                "  Leo@Local  ",
                "12345678901",
                "12345678",
                RoleUser.ROLE_USER
        );

        when(repo.existsByEmail("leo@local")).thenReturn(false);
        when(repo.existsByCpf("12345678901")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("HASH");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        when(repo.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        UserResponse resp = service.create(req);

        AppUser toSave = captor.getValue();
        assertEquals("Leandro", toSave.getName());
        assertEquals("leo@local", toSave.getEmail());
        assertEquals("12345678901", toSave.getCpf());
        assertEquals("HASH", toSave.getPasswordHash());
        assertEquals(RoleUser.ROLE_USER, toSave.getRole());

        assertNull(resp.id());
        assertEquals("Leandro", resp.name());
        assertEquals("leo@local", resp.email());

        verify(repo).existsByEmail("leo@local");
        verify(repo).existsByCpf("12345678901");
        verify(passwordEncoder).encode("12345678");
        verify(repo).save(any(AppUser.class));
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void createShouldFailWhenEmailAlreadyExists() {
        UserCreateRequest req = new UserCreateRequest("A", "A@LOCAL", "12345678901", "12345678", RoleUser.ROLE_USER);
        when(repo.existsByEmail("a@local")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertEquals("Email já cadastrado.", ex.getMessage());

        verify(repo).existsByEmail("a@local");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void createShouldFailWhenCpfAlreadyExists() {
        UserCreateRequest req = new UserCreateRequest("A", "a@local", "12345678901", "12345678", RoleUser.ROLE_USER);
        when(repo.existsByEmail("a@local")).thenReturn(false);
        when(repo.existsByCpf("12345678901")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertEquals("CPF já cadastrado.", ex.getMessage());

        verify(repo).existsByEmail("a@local");
        verify(repo).existsByCpf("12345678901");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void updateShouldChangeFieldsAndPasswordWhenProvided() {
        Long id = 1L;
        AppUser existing = new AppUser("Old", "old@local", "12345678901", "OLD_HASH", RoleUser.ROLE_USER);
        // seta id via reflexão pra simular entidade persistida
        setField(existing, "id", id);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.findByEmailIgnoreCase("new@local")).thenReturn(Optional.empty());
        when(repo.findByCpf("10987654321")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass123")).thenReturn("NEW_HASH");

        UserUpdateRequest req = new UserUpdateRequest("  New ", " NEW@LOCAL ", "10987654321", "newpass123");

        UserResponse resp = service.update(id, req);

        assertEquals("New", existing.getName());
        assertEquals("new@local", existing.getEmail());
        assertEquals("10987654321", existing.getCpf());
        assertEquals("NEW_HASH", existing.getPasswordHash());
        assertEquals(id, resp.id());

        verify(repo).findById(id);
        verify(repo).findByEmailIgnoreCase("new@local");
        verify(repo).findByCpf("10987654321");
        verify(passwordEncoder).encode("newpass123");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void updateShouldNotEncodePasswordWhenBlank() {
        Long id = 1L;
        AppUser existing = new AppUser("Old", "old@local", "12345678901", "OLD_HASH", RoleUser.ROLE_USER);
        setField(existing, "id", id);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.findByEmailIgnoreCase("new@local")).thenReturn(Optional.empty());
        when(repo.findByCpf("10987654321")).thenReturn(Optional.empty());

        UserUpdateRequest req = new UserUpdateRequest("New", "new@local", "10987654321", "   ");
        UserResponse resp = service.update(id, req);

        assertEquals("OLD_HASH", existing.getPasswordHash());
        assertEquals(id, resp.id());

        verify(repo).findById(id);
        verify(repo).findByEmailIgnoreCase("new@local");
        verify(repo).findByCpf("10987654321");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void updateShouldFailWhenUserNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, new UserUpdateRequest("A", "a@local", "12345678901", null)));
        assertEquals("Usuário não encontrado.", ex.getMessage());

        verify(repo).findById(1L);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void updateShouldFailWhenEmailBelongsToAnotherUser() {
        Long id = 1L;
        AppUser existing = new AppUser("Old", "old@local", "12345678901", "H", RoleUser.ROLE_USER);
        setField(existing, "id", id);
        AppUser other = new AppUser("Other", "new@local", "22222222222", "H2", RoleUser.ROLE_USER);
        setField(other, "id", 999L);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.findByEmailIgnoreCase("new@local")).thenReturn(Optional.of(other));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(id, new UserUpdateRequest("X", "new@local", "12345678901", null)));
        assertEquals("Email já cadastrado.", ex.getMessage());

        verify(repo).findById(id);
        verify(repo).findByEmailIgnoreCase("new@local");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void updateShouldFailWhenCpfBelongsToAnotherUser() {
        Long id = 1L;
        AppUser existing = new AppUser("Old", "old@local", "12345678901", "H", RoleUser.ROLE_USER);
        setField(existing, "id", id);
        AppUser other = new AppUser("Other", "x@local", "10987654321", "H2", RoleUser.ROLE_USER);
        setField(other, "id", 999L);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.findByEmailIgnoreCase("x@local")).thenReturn(Optional.empty());
        when(repo.findByCpf("10987654321")).thenReturn(Optional.of(other));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(id, new UserUpdateRequest("X", "x@local", "10987654321", null)));
        assertEquals("CPF já cadastrado.", ex.getMessage());

        verify(repo).findById(id);
        verify(repo).findByEmailIgnoreCase("x@local");
        verify(repo).findByCpf("10987654321");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void deleteShouldRemoveWhenExists() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).existsById(1L);
        verify(repo).deleteById(1L);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void deleteShouldFailWhenNotFound() {
        when(repo.existsById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
        assertEquals("Usuário não encontrado.", ex.getMessage());

        verify(repo).existsById(1L);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void getByIdShouldReturnResponse() {
        AppUser user = new AppUser("A", "a@local", "12345678901", "H", RoleUser.ROLE_USER);
        setField(user, "id", 10L);
        when(repo.findById(10L)).thenReturn(Optional.of(user));

        UserResponse resp = service.getById(10L);

        assertEquals(10L, resp.id());
        assertEquals("a@local", resp.email());
        verify(repo).findById(10L);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void listShouldMapAllUsers() {
        AppUser u1 = new AppUser("A", "a@local", "12345678901", "H", RoleUser.ROLE_USER);
        AppUser u2 = new AppUser("B", "b@local", "12345678901", "H", RoleUser.ROLE_ADMIN);
        when(repo.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponse> resp = service.list();

        assertEquals(2, resp.size());
        assertEquals("a@local", resp.get(0).email());
        assertEquals("b@local", resp.get(1).email());

        verify(repo).findAll();
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void changeRoleShouldUpdateRole() {
        AppUser user = new AppUser("A", "a@local", "12345678901", "H", RoleUser.ROLE_USER);
        setField(user, "id", 1L);
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        UserResponse resp = service.changeRole(1L, new UserRoleUpdateRequest(RoleUser.ROLE_ADMIN));

        assertEquals(RoleUser.ROLE_ADMIN, user.getRole());
        assertEquals(RoleUser.ROLE_ADMIN, resp.role());
        verify(repo).findById(1L);
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void getByEmailOrThrowShouldNormalizeEmail() {
        AppUser user = new AppUser("A", "a@local", "12345678901", "H", RoleUser.ROLE_USER);
        when(repo.findByEmailIgnoreCase("a@local")).thenReturn(Optional.of(user));

        AppUser found = service.getByEmailOrThrow("  A@LOCAL ");

        assertSame(user, found);
        verify(repo).findByEmailIgnoreCase("a@local");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void getByEmailOrThrowShouldFailWhenMissing() {
        when(repo.findByEmailIgnoreCase("a@local")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getByEmailOrThrow("a@local"));
        assertEquals("Usuário não encontrado.", ex.getMessage());

        verify(repo).findByEmailIgnoreCase("a@local");
        verifyNoMoreInteractions(repo, passwordEncoder);
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
