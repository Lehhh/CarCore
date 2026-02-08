package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.usecase.services.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    CarService carService;

    @InjectMocks
    CarController controller;

    @Test
    void createShouldDelegateToService() {
        CarCreateRequest req = new CarCreateRequest("Toyota", "Corolla", 2020, "Prata", new BigDecimal("100000.00"));
        CarResponse expected = new CarResponse(1L, req.brand(), req.model(), req.year(), req.color(), req.price());

        when(carService.create(req)).thenReturn(expected);

        CarResponse resp = controller.create(req);

        assertSame(expected, resp);
        verify(carService).create(req);
        verifyNoMoreInteractions(carService);
    }

    @Test
    void updateShouldDelegateToService() {
        Long id = 10L;
        CarUpdateRequest req = new CarUpdateRequest("Honda", "Civic", 2019, "Preto", new BigDecimal("90000.00"));
        CarResponse expected = new CarResponse(id, req.brand(), req.model(), req.year(), req.color(), req.price());

        when(carService.update(id, req)).thenReturn(expected);

        CarResponse resp = controller.update(id, req);

        assertSame(expected, resp);
        verify(carService).update(id, req);
        verifyNoMoreInteractions(carService);
    }

    @Test
    void getByIdShouldDelegateToService() {
        Long id = 99L;
        CarResponse expected = new CarResponse(id, "BMW", "320i", 2022, "Azul", new BigDecimal("250000.00"));

        when(carService.getById(id)).thenReturn(expected);

        CarResponse resp = controller.getById(id);

        assertSame(expected, resp);
        verify(carService).getById(id);
        verifyNoMoreInteractions(carService);
    }
}
