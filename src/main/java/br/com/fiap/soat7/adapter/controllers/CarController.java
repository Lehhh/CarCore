package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.dto.car.CarCreateRequest;
import br.com.fiap.soat7.data.dto.car.CarResponse;
import br.com.fiap.soat7.data.dto.car.CarUpdateRequest;
import br.com.fiap.soat7.usecase.services.CarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/1/car")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CarController {

    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse create(@Valid @RequestBody CarCreateRequest req) {
        var authInfo = authInfo();

        log.info("[CAR][CREATE] request user={} authorities={} brand={} model={} year={} color={}",
                authInfo.user, authInfo.authorities, req.brand(), req.model(), req.year(), req.color());

        try {
            CarResponse created = carService.create(req);

            // se CarResponse tiver id, loga. Se não tiver, mantém simples.
            log.info("[CAR][CREATE] success user={} authorities={}", authInfo.user, authInfo.authorities);

            return created;
        } catch (Exception ex) {
            log.error("[CAR][CREATE] failed user={} authorities={} msg={}",
                    authInfo.user, authInfo.authorities, ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public CarResponse update(@PathVariable Long id, @Valid @RequestBody CarUpdateRequest req) {
        var authInfo = authInfo();

        log.info("[CAR][UPDATE] request id={} user={} authorities={} brand={} model={} year={} color={}",
                id, authInfo.user, authInfo.authorities, req.brand(), req.model(), req.year(), req.color());

        try {
            CarResponse updated = carService.update(id, req);
            log.info("[CAR][UPDATE] success id={} user={} authorities={}", id, authInfo.user, authInfo.authorities);
            return updated;
        } catch (Exception ex) {
            log.error("[CAR][UPDATE] failed id={} user={} authorities={} msg={}",
                    id, authInfo.user, authInfo.authorities, ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public CarResponse getById(@PathVariable Long id) {
        var authInfo = authInfo();
        log.info("[CAR][GET] request id={} user={} authorities={}", id, authInfo.user, authInfo.authorities);

        try {
            CarResponse resp = carService.getById(id);
            log.info("[CAR][GET] success id={} user={} authorities={}", id, authInfo.user, authInfo.authorities);
            return resp;
        } catch (Exception ex) {
            log.error("[CAR][GET] failed id={} user={} authorities={} msg={}",
                    id, authInfo.user, authInfo.authorities, ex.getMessage(), ex);
            throw ex;
        }
    }

    private AuthInfo authInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return new AuthInfo("anonymous", "[]");

        String user = auth.getName(); // normalmente subject
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",", "[", "]"));

        return new AuthInfo(user, authorities);
    }

    private record AuthInfo(String user, String authorities) {}
}
