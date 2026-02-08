package br.com.fiap.soat7.data.dto.user;


import br.com.fiap.soat7.data.RoleUser;
import jakarta.validation.constraints.*;

public record UserCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos numéricos") String cpf,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank RoleUser role
) {}
