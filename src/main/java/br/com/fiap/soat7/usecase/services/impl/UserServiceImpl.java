package br.com.fiap.soat7.usecase.services.impl;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import br.com.fiap.soat7.data.dto.user.*;
import br.com.fiap.soat7.usecase.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest req) {
        String email = req.email().toLowerCase().trim();
        String cpf = req.cpf().trim();

        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        if (repo.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        String hash = passwordEncoder.encode(req.password());

        AppUser saved = repo.save(new AppUser(
                req.name().trim(),
                email,
                cpf,
                hash,
                RoleUser.ROLE_USER
        ));

        return UserResponse.from(saved);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest req) {
        AppUser user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        String email = req.email().toLowerCase().trim();
        String cpf = req.cpf().trim();

        // Evita colisão quando editar para email/cpf de outro usuário
        repo.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new IllegalArgumentException("Email já cadastrado.");
        });
        repo.findByCpf(cpf).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new IllegalArgumentException("CPF já cadastrado.");
        });

        user.update(req.name().trim(), email, cpf);

        if (req.password() != null && !req.password().isBlank()) {
            user.changePassword(passwordEncoder.encode(req.password()));
        }

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        repo.deleteById(id);
    }

    @Override
    public UserResponse getById(Long id) {
        AppUser user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        return UserResponse.from(user);
    }

    @Override
    public List<UserResponse> list() {
        return repo.findAll().stream().map(UserResponse::from).toList();
    }

    @Override
    @Transactional
    public UserResponse changeRole(Long id, UserRoleUpdateRequest req) {
        AppUser user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        user.changeRole(req.role());
        return UserResponse.from(user);
    }

    @Override
    public AppUser getByEmailOrThrow(String email) {
        return repo.findByEmailIgnoreCase(email.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }
}
