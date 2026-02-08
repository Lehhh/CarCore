package br.com.fiap.soat7.usecase.services.impl;

import br.com.fiap.soat7.adapter.repositories.CarRepository;
import br.com.fiap.soat7.data.domain.Car;
import br.com.fiap.soat7.data.dto.car.*;
import br.com.fiap.soat7.infra.config.api.CarStoreViewClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    CarRepository carRepo;

    @Mock
    CarStoreViewClient viewClient;

    CarServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CarServiceImpl(carRepo, viewClient);
    }

    @Test
    void createShouldSaveAndReplicateToView() {
        CarCreateRequest req = new CarCreateRequest("Toyota", "Corolla", 2020, "Prata", new BigDecimal("100000.00"));

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        when(carRepo.save(carCaptor.capture())).thenAnswer(inv -> {
            Car c = carCaptor.getValue();
            setField(c, "id", 1L);
            return c;
        });

        ArgumentCaptor<CarSyncRequest> syncCaptor = ArgumentCaptor.forClass(CarSyncRequest.class);
        doNothing().when(viewClient).upsertCar(syncCaptor.capture());

        CarResponse resp = service.create(req);

        assertEquals(1L, resp.id());
        assertEquals(req.brand(), resp.brand());

        Car savedCar = carCaptor.getValue();
        assertEquals("Toyota", savedCar.getBrand());

        CarSyncRequest sync = syncCaptor.getValue();
        assertEquals(1L, sync.id());
        assertEquals("Toyota", sync.brand());
        assertNotNull(sync.updatedAt());

        verify(carRepo).save(any(Car.class));
        verify(viewClient).upsertCar(any(CarSyncRequest.class));
        verifyNoMoreInteractions(carRepo, viewClient);
    }

    @Test
    void updateShouldUpdateExistingAndReplicateToView() {
        Long id = 10L;
        Car existing = new Car("Ford", "Ka", 2018, "Branco", new BigDecimal("30000.00"));
        setField(existing, "id", id);

        when(carRepo.findById(id)).thenReturn(Optional.of(existing));
        when(carRepo.save(existing)).thenReturn(existing);

        CarUpdateRequest req = new CarUpdateRequest("Honda", "Civic", 2019, "Preto", new BigDecimal("90000.00"));

        ArgumentCaptor<CarSyncRequest> syncCaptor = ArgumentCaptor.forClass(CarSyncRequest.class);
        doNothing().when(viewClient).upsertCar(syncCaptor.capture());

        CarResponse resp = service.update(id, req);

        assertEquals(id, resp.id());
        assertEquals("Honda", resp.brand());
        assertEquals("Civic", resp.model());

        assertEquals("Honda", existing.getBrand());
        assertEquals("Civic", existing.getModel());

        CarSyncRequest sync = syncCaptor.getValue();
        assertEquals(id, sync.id());
        assertEquals("Honda", sync.brand());
        assertTrue(sync.updatedAt().isBefore(Instant.now().plusSeconds(5)));

        verify(carRepo).findById(id);
        verify(carRepo).save(existing);
        verify(viewClient).upsertCar(any(CarSyncRequest.class));
        verifyNoMoreInteractions(carRepo, viewClient);
    }

    @Test
    void updateShouldFailWhenCarNotFound() {
        when(carRepo.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, new CarUpdateRequest("A", "B", 2000, "C", new BigDecimal("1.00"))));
        assertEquals("Car não encontrado", ex.getMessage());

        verify(carRepo).findById(1L);
        verifyNoMoreInteractions(carRepo, viewClient);
    }

    @Test
    void getByIdShouldReturnResponse() {
        Car car = new Car("VW", "Gol", 2015, "Vermelho", new BigDecimal("20000.00"));
        setField(car, "id", 7L);
        when(carRepo.findById(7L)).thenReturn(Optional.of(car));

        CarResponse resp = service.getById(7L);

        assertEquals(7L, resp.id());
        assertEquals("VW", resp.brand());

        verify(carRepo).findById(7L);
        verifyNoMoreInteractions(carRepo, viewClient);
    }

    @Test
    void getByIdShouldFailWhenMissing() {
        when(carRepo.findById(7L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(7L));
        assertEquals("Car não encontrado", ex.getMessage());

        verify(carRepo).findById(7L);
        verifyNoMoreInteractions(carRepo, viewClient);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
