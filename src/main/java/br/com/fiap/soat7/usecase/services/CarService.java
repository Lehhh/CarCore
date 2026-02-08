package br.com.fiap.soat7.usecase.services;

import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarSellRequest;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;

public interface CarService {

    CarResponse create(CarCreateRequest carCreateRequest);
    CarResponse update(Long carId, CarUpdateRequest carUpdateRequest);
    CarResponse sell(Long carId, CarSellRequest carSellRequest);
    CarResponse getById(Long carId);

}
