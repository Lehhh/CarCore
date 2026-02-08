package br.com.fiap.soat7.usecase.services.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarNotFoundExceptionTest {

    @Test
    void messageShouldContainId() {
        CarNotFoundException ex = new CarNotFoundException(99L);
        assertTrue(ex.getMessage().contains("id=99"));
    }
}
