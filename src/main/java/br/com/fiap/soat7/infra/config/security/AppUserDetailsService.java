package br.com.fiap.soat7.infra.config.security;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repo;

    public AppUserDetailsService(AppUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = repo.findByEmailIgnoreCase(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        System.out.println("loadUserByUsername: " + user.getEmail() + ", role: " + user.getRole());
        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name().replace("ROLE_", "")) // ROLE_USER -> USER
                .build();
    }
}

