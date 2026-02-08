package br.com.fiap.soat7.data.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void constructorAndUpdateShouldSetFields() {
        Car car = new Car("VW", "Gol", 2015, "Vermelho", new BigDecimal("20000.00"));

        assertEquals("VW", car.getBrand());
        assertEquals("Gol", car.getModel());

        car.update("Honda", "Civic", 2019, "Preto", new BigDecimal("90000.00"));

        assertEquals("Honda", car.getBrand());
        assertEquals("Civic", car.getModel());
        assertEquals(2019, car.getYear());
        assertEquals("Preto", car.getColor());
        assertEquals(new BigDecimal("90000.00"), car.getPrice());
    }
}
