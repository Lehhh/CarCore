package br.com.fiap.soat7.usecase.services.impl;

import br.com.fiap.soat7.adapter.repositories.CarRepository;
import br.com.fiap.soat7.adapter.repositories.SalesRepository;
import br.com.fiap.soat7.data.domain.Car;
import br.com.fiap.soat7.data.domain.Sales;
import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarSellRequest;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.usecase.services.exceptions.CarNotFoundException;
import br.com.fiap.soat7.usecase.services.CarService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final SalesRepository salesRepository;

    @Override
    public CarResponse create(CarCreateRequest carCreateRequest) {
        Car v = new Car(carCreateRequest.brand(), carCreateRequest.model(), carCreateRequest.year(), carCreateRequest.color(), carCreateRequest.price());
        Car saved = carRepository.save(v);
        return CarResponse.from(saved);
    }

    @Override
    public CarResponse update(Long carId, CarUpdateRequest carUpdateRequest) {
        Car v = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        v.update(carUpdateRequest.brand(), carUpdateRequest.model(), carUpdateRequest.year(), carUpdateRequest.color(), carUpdateRequest.price());
        return CarResponse.from(v);
    }


    @Override
    @Transactional
    public CarResponse sell(Long carId, CarSellRequest req) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        if (salesRepository.existsByCarId(car.getId())) {
            throw new IllegalStateException("Veículo já vendido.");
        }

        car.markAsSold();
        Sales sale = salesRepository.save(new Sales(car, req.buyerCpf(), req.soldAt()));

        return CarResponse.from(car, sale);
    }

    @Override
    public CarResponse getById(Long carId) {
        Car v = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        return CarResponse.from(v);
    }
}
