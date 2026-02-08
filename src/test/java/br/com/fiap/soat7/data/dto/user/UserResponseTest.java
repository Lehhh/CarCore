package br.com.fiap.soat7.data.dto.user;

import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void fromShouldMapDomainEntity() {
        AppUser user = new AppUser("Leandro", "leo@local", "12345678901", "H", RoleUser.ROLE_USER);
        setField(user, "id", 42L);

        UserResponse resp = UserResponse.from(user);

        assertEquals(42L, resp.id());
        assertEquals("Leandro", resp.name());
        assertEquals("leo@local", resp.email());
        assertEquals("12345678901", resp.cpf());
        assertEquals(RoleUser.ROLE_USER, resp.role());
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
