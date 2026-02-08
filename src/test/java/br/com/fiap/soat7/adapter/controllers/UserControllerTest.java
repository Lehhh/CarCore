package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.dto.user.*;
import br.com.fiap.soat7.usecase.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService service;

    UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(service);
    }

    @Test
    void createShouldDelegateToService() {
        UserCreateRequest req = new UserCreateRequest("Leandro", "leo@local", "12345678901", "12345678", RoleUser.ROLE_USER);
        UserResponse expected = new UserResponse(1L, "Leandro", "leo@local", "12345678901", RoleUser.ROLE_USER);
        when(service.create(req)).thenReturn(expected);

        UserResponse resp = controller.create(req);

        assertSame(expected, resp);
        verify(service).create(req);
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateShouldDelegateToService() {
        Long id = 2L;
        UserUpdateRequest req = new UserUpdateRequest("Leo", "leo@local", "12345678901", "");
        UserResponse expected = new UserResponse(id, "Leo", "leo@local", "12345678901", RoleUser.ROLE_USER);
        when(service.update(id, req)).thenReturn(expected);

        UserResponse resp = controller.update(id, req);

        assertSame(expected, resp);
        verify(service).update(id, req);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deleteShouldDelegateToService() {
        Long id = 3L;
        controller.delete(id);
        verify(service).delete(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getByIdShouldDelegateToService() {
        Long id = 4L;
        UserResponse expected = new UserResponse(id, "X", "x@local", "12345678901", RoleUser.ROLE_USER);
        when(service.getById(id)).thenReturn(expected);

        UserResponse resp = controller.getById(id);

        assertSame(expected, resp);
        verify(service).getById(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void listShouldDelegateToService() {
        List<UserResponse> expected = List.of(
                new UserResponse(1L, "A", "a@local", "12345678901", RoleUser.ROLE_USER),
                new UserResponse(2L, "B", "b@local", "12345678901", RoleUser.ROLE_ADMIN)
        );
        when(service.list()).thenReturn(expected);

        List<UserResponse> resp = controller.list();

        assertSame(expected, resp);
        verify(service).list();
        verifyNoMoreInteractions(service);
    }

    @Test
    void changeRoleShouldDelegateToService() {
        Long id = 5L;
        UserRoleUpdateRequest req = new UserRoleUpdateRequest(RoleUser.ROLE_ADMIN);
        UserResponse expected = new UserResponse(id, "A", "a@local", "12345678901", RoleUser.ROLE_ADMIN);
        when(service.changeRole(id, req)).thenReturn(expected);

        UserResponse resp = controller.changeRole(id, req);

        assertSame(expected, resp);
        verify(service).changeRole(id, req);
        verifyNoMoreInteractions(service);
    }
}
