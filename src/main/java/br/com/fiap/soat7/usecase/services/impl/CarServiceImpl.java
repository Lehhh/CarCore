package br.com.fiap.soat7.usecase.services.impl;

import br.com.fiap.soat7.adapter.repositories.CarRepository;
import br.com.fiap.soat7.data.domain.Car;
import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarSyncRequest;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.infra.config.api.CarStoreViewClient;
import br.com.fiap.soat7.usecase.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepo;
    private final CarStoreViewClient viewClient;

    @Override
    @Transactional
    public CarResponse create(CarCreateRequest req) {
        Car car = new Car(
                req.brand(),
                req.model(),
                req.year(),
                req.color(),
                req.price()
        );

        Car saved = carRepo.save(car);

        // replica pro View (idempotência por updatedAt)
        viewClient.upsertCar(new CarSyncRequest(
                saved.getId(),
                saved.getBrand(),
                saved.getModel(),
                saved.getYear(),
                saved.getColor(),
                saved.getPrice(),
                Instant.now()
        ));

        return toResponse(saved);
    }

    @Override
    @Transactional
    public CarResponse update(Long carId, CarUpdateRequest req) {
        Car existing = carRepo.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car não encontrado"));

        // usa seu método de domínio
        existing.update(
                req.brand(),
                req.model(),
                req.year(),
                req.color(),
                req.price()
        );

        Car saved = carRepo.save(existing);

        // replica pro View
        viewClient.upsertCar(new CarSyncRequest(
                saved.getId(),
                saved.getBrand(),
                saved.getModel(),
                saved.getYear(),
                saved.getColor(),
                saved.getPrice(),
                Instant.now()
        ));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponse getById(Long carId) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car não encontrado"));

        return toResponse(car);
    }

    private CarResponse toResponse(Car car) {
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
