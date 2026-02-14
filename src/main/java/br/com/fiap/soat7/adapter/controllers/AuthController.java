package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.dto.user.LoginRequest;
import br.com.fiap.soat7.infra.config.security.JwtIssuer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtIssuer jwtIssuer;
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest body) throws Exception {
        var user = userRepo.findByEmailIgnoreCase(body.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!passwordEncoder.matches(body.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        RoleUser role = user.getRole(); // ex: ["ROLE_ADMIN"]
        String token = jwtIssuer.issue(user.getEmail(), user.getEmail(), role, 3600);
        System.out.println("User " + user.getEmail() + " logged in with role " + role + ", issued token: " + token);

        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
