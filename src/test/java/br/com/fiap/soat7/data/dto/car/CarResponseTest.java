package br.com.fiap.soat7.data.dto.car;

import br.com.fiap.soat7.data.domain.Car;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarResponseTest {

    @Test
    void fromShouldMapDomainEntity() {
        Car car = new Car("VW", "Gol", 2015, "Vermelho", new BigDecimal("20000.00"));
        setField(car, "id", 7L);

        CarResponse resp = CarResponse.from(car);

        assertEquals(7L, resp.id());
        assertEquals("VW", resp.brand());
        assertEquals("Gol", resp.model());
        assertEquals(2015, resp.year());
        assertEquals("Vermelho", resp.color());
        assertEquals(new BigDecimal("20000.00"), resp.price());
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
