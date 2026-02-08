package br.com.fiap.soat7.data.dto.car;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarUpdateRequestTest {

    @Test
    void recordShouldExposeFields() {
        CarUpdateRequest req = new CarUpdateRequest("Honda", "Civic", 2019, "Preto", new BigDecimal("90000.00"));

        assertEquals("Honda", req.brand());
        assertEquals("Civic", req.model());
        assertEquals(2019, req.year());
        assertEquals("Preto", req.color());
        assertEquals(new BigDecimal("90000.00"), req.price());
    }
}