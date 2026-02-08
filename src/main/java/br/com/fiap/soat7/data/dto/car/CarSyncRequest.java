package br.com.fiap.soat7.data.dto.car;

import java.math.BigDecimal;
import java.time.Instant;

public record CarSyncRequest(
        Long id,
        String brand,
        String model,
        Integer year,
        String color,
        BigDecimal price,
        Instant updatedAt
) {}
