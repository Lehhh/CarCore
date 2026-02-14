package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.usecase.services.CarService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    CarService carService;

    @InjectMocks
    CarController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

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

    // -----------------------
    // CREATE
    // -----------------------

    @Test
    void create_success_withAuth() {
        setAuth("user-123", List.of("ROLE_ADMIN", "SCOPE_write"));

        CarCreateRequest req = mock(CarCreateRequest.class);
        when(req.brand()).thenReturn("Ford");
        when(req.model()).thenReturn("Ka");
        when(req.year()).thenReturn(2019);
        when(req.color()).thenReturn("Prata");

        CarResponse expected = mock(CarResponse.class);
        when(carService.create(req)).thenReturn(expected);

        CarResponse actual = controller.create(req);

        assertSame(expected, actual);
        verify(carService).create(req);
    }

    @Test
    void create_failed_withAuth_rethrows() {
        setAuth("user-123", List.of("ROLE_ADMIN"));

        CarCreateRequest req = mock(CarCreateRequest.class);
        when(req.brand()).thenReturn("VW");
        when(req.model()).thenReturn("Golf");
        when(req.year()).thenReturn(2020);
        when(req.color()).thenReturn("Preto");

        RuntimeException ex = new RuntimeException("boom");
        when(carService.create(req)).thenThrow(ex);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> controller.create(req));
        assertSame(ex, thrown);
        verify(carService).create(req);
    }

    @Test
    void create_success_whenAuthenticationIsNull_coversAnonymousBranch() {
        // força Authentication == null dentro do SecurityContext
        SecurityContextHolder.setContext(new SecurityContextImpl());

        CarCreateRequest req = mock(CarCreateRequest.class);
        when(req.brand()).thenReturn("Fiat");
        when(req.model()).thenReturn("Argo");
        when(req.year()).thenReturn(2021);
        when(req.color()).thenReturn("Vermelho");

        CarResponse expected = mock(CarResponse.class);
        when(carService.create(req)).thenReturn(expected);

        CarResponse actual = controller.create(req);

        assertSame(expected, actual);
        verify(carService).create(req);
    }

    // -----------------------
    // UPDATE
    // -----------------------

    @Test
    void update_success_withAuth() {
        setAuth("user-999", List.of("ROLE_MANAGER", "SCOPE_edit"));

        Long id = 10L;

        CarUpdateRequest req = mock(CarUpdateRequest.class);
        when(req.brand()).thenReturn("Honda");
        when(req.model()).thenReturn("Civic");
        when(req.year()).thenReturn(2018);
        when(req.color()).thenReturn("Cinza");

        CarResponse expected = mock(CarResponse.class);
        when(carService.update(id, req)).thenReturn(expected);

        CarResponse actual = controller.update(id, req);

        assertSame(expected, actual);
        verify(carService).update(id, req);
    }

    @Test
    void update_failed_withAuth_rethrows() {
        setAuth("user-999", List.of("ROLE_MANAGER"));

        Long id = 11L;

        CarUpdateRequest req = mock(CarUpdateRequest.class);
        when(req.brand()).thenReturn("BMW");
        when(req.model()).thenReturn("320i");
        when(req.year()).thenReturn(2022);
        when(req.color()).thenReturn("Azul");

        IllegalStateException ex = new IllegalStateException("cannot update");
        when(carService.update(id, req)).thenThrow(ex);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> controller.update(id, req));
        assertSame(ex, thrown);
        verify(carService).update(id, req);
    }

    // -----------------------
    // GET BY ID
    // -----------------------

    @Test
    void getById_success_withAuth() {
        setAuth("user-abc", List.of("ROLE_USER", "SCOPE_read"));

        Long id = 1L;

        CarResponse expected = mock(CarResponse.class);
        when(carService.getById(id)).thenReturn(expected);

        CarResponse actual = controller.getById(id);

        assertSame(expected, actual);
        verify(carService).getById(id);
    }

    @Test
    void getById_failed_withAuth_rethrows() {
        setAuth("user-abc", List.of("ROLE_USER"));

        Long id = 2L;

        RuntimeException ex = new RuntimeException("not found");
        when(carService.getById(id)).thenThrow(ex);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> controller.getById(id));
        assertSame(ex, thrown);
        verify(carService).getById(id);
    }

    // -----------------------
    // Helpers
    // -----------------------

    private static void setAuth(String username, List<String> authorities) {
        var auths = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        var auth = new UsernamePasswordAuthenticationToken(username, "N/A", auths);
        var ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

}
