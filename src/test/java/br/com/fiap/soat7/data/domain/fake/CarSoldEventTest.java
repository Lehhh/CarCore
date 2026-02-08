package br.com.fiap.soat7.data.domain.fake;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CarSoldEventTest {

    @Test
    void recordShouldExposeFields() {
        Instant now = Instant.now();
        CarSoldEvent e = new CarSoldEvent(1L, "12345678901", now, "PIX");

        assertEquals(1L, e.carId());
        assertEquals("12345678901", e.buyerCpf());
        assertEquals(now, e.soldAt());
        assertEquals("PIX", e.paymentCode());
    }
}
