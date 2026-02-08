package br.com.fiap.soat7.data.dto.car;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CarSyncRequestTest {

    @Test
    void recordShouldExposeFields() {
        Instant now = Instant.now();
        CarSyncRequest req = new CarSyncRequest(1L, "A", "B", 2000, "C", new BigDecimal("1.00"), now);

        assertEquals(1L, req.id());
        assertEquals("A", req.brand());
        assertEquals("B", req.model());
        assertEquals(2000, req.year());
        assertEquals("C", req.color());
        assertEquals(new BigDecimal("1.00"), req.price());
        assertEquals(now, req.updatedAt());
    }
}
