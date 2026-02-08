package br.com.fiap.soat7.data.dto.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterRequestTest {

    @Test
    void recordShouldExposeFields() {
        UserRegisterRequest req = new UserRegisterRequest("A", "a@local", "12345678901", "12345678");

        assertEquals("A", req.name());
        assertEquals("a@local", req.email());
        assertEquals("12345678901", req.cpf());
        assertEquals("12345678", req.password());
    }
}
