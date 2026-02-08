package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.domain.fake.CarSoldEvent;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InternalCarSalesControllerTest {

    @Test
    void markSoldShouldReturnNoContent() {
        InternalCarSalesController controller = new InternalCarSalesController();

        CarSoldEvent event = new CarSoldEvent(10L, "12345678901", Instant.now(), "PIX");
        ResponseEntity<Void> resp = controller.markSold(10L, event);

        assertEquals(204, resp.getStatusCode().value());
        assertNull(resp.getBody());
    }
}
