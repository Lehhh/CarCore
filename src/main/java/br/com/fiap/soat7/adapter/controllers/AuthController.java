package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.infra.config.security.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtIssuer jwtIssuer;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) throws Exception {
        // fake: validação real você faz com user/senha do seu jeito
        String email = body.getOrDefault("email", "user@local");
        String subject = email;

        String token = jwtIssuer.issue(subject, email, List.of("USER"), 3600);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
