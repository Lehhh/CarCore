package br.com.fiap.soat7.data.dto.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateRequestTest {

    @Test
    void recordShouldExposeFields() {
        UserUpdateRequest req = new UserUpdateRequest("A", "a@local", "12345678901", null);

        assertEquals("A", req.name());
        assertEquals("a@local", req.email());
        assertEquals("12345678901", req.cpf());
        assertNull(req.password());
    }
}
