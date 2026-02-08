package br.com.fiap.soat7.data.dto.car;

import br.com.fiap.soat7.data.domain.Car;
import br.com.fiap.soat7.data.domain.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CarResponse(
        Long id,
        String brand,
        String model,
        Integer year,
        String color,
        BigDecimal price,
        boolean sold,
        String buyerCpf,
        LocalDateTime soldAt
) {
    public static CarResponse from(Car car, Sales sale) {
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getPrice(),
                car.isSold(),
                sale != null ? sale.getBuyerCpf() : null,
                sale != null ? sale.getSoldAt() : null
        );
    }

    public static CarResponse from(Car car) {
        return from(car, null);
    }
}
