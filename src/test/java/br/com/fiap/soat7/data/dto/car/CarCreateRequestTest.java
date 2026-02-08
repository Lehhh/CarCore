package br.com.fiap.soat7.data.dto.car;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarCreateRequestTest {

    @Test
    void recordShouldExposeFields() {
        CarCreateRequest req = new CarCreateRequest("Toyota", "Corolla", 2020, "Prata", new BigDecimal("100000.00"));

        assertEquals("Toyota", req.brand());
        assertEquals("Corolla", req.model());
        assertEquals(2020, req.year());
        assertEquals("Prata", req.color());
        assertEquals(new BigDecimal("100000.00"), req.price());
    }
}
