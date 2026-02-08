package br.com.fiap.soat7.usecase.services;

import br.com.fiap.soat7.data.domain.AppUser;
import br.com.fiap.soat7.data.dto.user.UserResponse;
import br.com.fiap.soat7.data.dto.user.*;

import java.util.List;

public interface UserService {

    // CRUD
    UserResponse create(UserCreateRequest req);
    UserResponse update(Long id, UserUpdateRequest req);
    void delete(Long id);

    UserResponse getById(Long id);
    List<UserResponse> list();

    // roles
    UserResponse changeRole(Long id, UserRoleUpdateRequest req);

    // support auth
    AppUser getByEmailOrThrow(String email);
}