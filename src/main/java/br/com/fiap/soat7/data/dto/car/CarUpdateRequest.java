package br.com.fiap.soat7.data.dto.car;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CarUpdateRequest(
        @NotBlank @Size(max = 60) String brand,
        @NotBlank @Size(max = 80) String model,
        @NotNull @Min(1886) Integer year,
        @NotBlank @Size(max = 30) String color,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price
) {}
