package br.com.fiap.soat7.infra;

import br.com.fiap.soat7.adapter.repositories.AppUserRepository;
import br.com.fiap.soat7.data.RoleUser;
import br.com.fiap.soat7.data.domain.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class AdminUserInitializer implements CommandLineRunner {

    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Value("${br.com.fiap.admin.name}")
    private String name;

    @Value("${br.com.fiap.admin.email}")
    private String email;

    @Value("${br.com.fiap.admin.cpf}")
    private String cpf;

    @Value("${br.com.fiap.admin.password}")
    private String password;

    @Override
    public void run(String... args) {
        if (repo.existsByEmail(email.toLowerCase())) {
            log.info("Usuário admin já existe. Ignorando bootstrap.");
            return;
        }
        cpf = cpf == null ? "" : cpf.replaceAll("\\D", "");

        AppUser admin = new AppUser(
                name,
                email.toLowerCase(),
                cpf,
                passwordEncoder.encode(password),
                RoleUser.ROLE_ADMIN
        );

        repo.save(admin);

        log.warn("Usuário ADMIN criado automaticamente: {}", email);
    }
}
