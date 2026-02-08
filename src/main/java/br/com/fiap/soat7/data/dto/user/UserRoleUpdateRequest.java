package br.com.fiap.soat7.data.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRoleUpdateRequest(

        @NotBlank
        @Pattern(
                regexp = "ROLE_[A-Z_]+",
                message = "Role inválida. Ex: ROLE_USER, ROLE_ADMIN"
        )
        String role
) {}
