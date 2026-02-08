package br.com.fiap.soat7.data.dto.car;

import br.com.fiap.soat7.data.domain.Car;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarResponse(
        Long id,
        String brand,
        String model,
        Integer year,
        String color,
        BigDecimal price
) {
    public static CarResponse from(Car car) {
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getPrice()
        );
    }
}
