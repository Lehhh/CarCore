package br.com.fiap.soat7.adapter.repositories;

import br.com.fiap.soat7.data.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    Optional<AppUser> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}