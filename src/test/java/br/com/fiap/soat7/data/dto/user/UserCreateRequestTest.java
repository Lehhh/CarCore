package br.com.fiap.soat7.data.dto.user;

import br.com.fiap.soat7.data.RoleUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateRequestTest {

    @Test
    void recordShouldExposeFields() {
        UserCreateRequest req = new UserCreateRequest("A", "a@local", "12345678901", "12345678", RoleUser.ROLE_USER);

        assertEquals("A", req.name());
        assertEquals("a@local", req.email());
        assertEquals("12345678901", req.cpf());
        assertEquals("12345678", req.password());
        assertEquals(RoleUser.ROLE_USER, req.role());
    }
}
