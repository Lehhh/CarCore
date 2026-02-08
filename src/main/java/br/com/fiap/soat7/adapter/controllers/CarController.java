package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarSellRequest;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.usecase.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse create(@Valid @RequestBody CarCreateRequest req) {
        return carService.create(req);
    }

    @PutMapping("/{id}")
    public CarResponse update(@PathVariable Long id, @Valid @RequestBody CarUpdateRequest req) {
        return carService.update(id, req);
    }

    @GetMapping("/{id}")
    public CarResponse getById(@PathVariable Long id) {
        return carService.getById(id);
    }
}
