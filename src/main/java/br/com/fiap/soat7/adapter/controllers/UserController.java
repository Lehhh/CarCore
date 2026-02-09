package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.dto.user.*;
import br.com.fiap.soat7.usecase.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 🔒 SOMENTE ADMIN pode criar usuário
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return service.create(req);
    }

    // 🔒 Apenas ADMIN pode editar dados
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
        return service.update(id, req);
    }

    // 🔒 Apenas ADMIN pode deletar
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // 👀 leitura liberada (ou use hasAnyRole se quiser restringir)
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.list();
    }

    // 🔒 Apenas ADMIN pode mudar role
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public UserResponse changeRole(@PathVariable Long id,
                                   @Valid @RequestBody UserRoleUpdateRequest req) {
        return service.changeRole(id, req);
    }
}
