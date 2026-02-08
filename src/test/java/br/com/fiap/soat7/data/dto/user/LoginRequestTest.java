package br.com.fiap.soat7.data.dto.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void recordShouldExposeFields() {
        LoginRequest req = new LoginRequest("a@local", "pass");

        assertEquals("a@local", req.email());
        assertEquals("pass", req.password());
    }
}
