package br.com.fiap.soat7.data.dto.user;

import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;

public record UserResponse(
        Long id,
        String name,
        String email,
        String cpf,
        RoleUser role
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }
}

