package br.com.fiap.soat7.data.domain.fake;

import java.time.Instant;

public record CarSoldEvent(
        Long carId,
        String buyerCpf,
        Instant soldAt,
        String paymentCode
) {}
