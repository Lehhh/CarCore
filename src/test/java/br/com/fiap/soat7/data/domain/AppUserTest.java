package br.com.fiap.soat7.data.domain;

import br.com.fiap.soat7.data.RoleUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    void domainMethodsShouldUpdateFields() {
        AppUser user = new AppUser("Old", "old@local", "12345678901", "H", RoleUser.ROLE_USER);

        user.update("New", "new@local", "10987654321");
        user.changePassword("NEW_HASH");
        user.changeRole(RoleUser.ROLE_ADMIN);

        assertEquals("New", user.getName());
        assertEquals("new@local", user.getEmail());
        assertEquals("10987654321", user.getCpf());
        assertEquals("NEW_HASH", user.getPasswordHash());
        assertEquals(RoleUser.ROLE_ADMIN, user.getRole());
    }

    @Test
    void normalizeCpfShouldStripNonDigitsAndRequire11Digits() {
        AppUser user = new AppUser("A", "a@local", "123.456.789-01", "H", RoleUser.ROLE_USER);

        invokeNormalizeCpf(user);

        assertEquals("12345678901", user.getCpf());
    }

    @Test
    void normalizeCpfShouldThrowWhenCpfIsNull() {
        AppUser user = new AppUser("A", "a@local", null, "H", RoleUser.ROLE_USER);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> invokeNormalizeCpf(user));
        assertEquals("CPF não pode ser nulo", ex.getMessage());
    }

    @Test
    void normalizeCpfShouldThrowWhenCpfIsNot11Digits() {
        AppUser user = new AppUser("A", "a@local", "123", "H", RoleUser.ROLE_USER);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> invokeNormalizeCpf(user));
        assertEquals("CPF deve conter 11 dígitos", ex.getMessage());
    }

    private static void invokeNormalizeCpf(AppUser user) {
        try {
            var m = AppUser.class.getDeclaredMethod("normalizeCpf");
            m.setAccessible(true);
            try {
                m.invoke(user);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                if (ite.getCause() instanceof RuntimeException re) throw re;
                throw new RuntimeException(ite.getCause());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
