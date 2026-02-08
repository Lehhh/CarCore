package br.com.fiap.soat7.data.domain;

import br.com.fiap.soat7.data.RoleUser;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(
        name = "app_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_app_users_cpf", columnNames = "cpf")
        }
)
@Getter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private RoleUser role;

    protected AppUser() {}


    public AppUser(String name, String email, String cpf, String passwordHash, RoleUser role) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /* métodos de domínio simples */
    public void update(String name, String email, String cpf) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void changeRole(RoleUser role) {
        this.role = role;
    }

    /* segurança extra: garante normalização antes de persistir */
    @PrePersist
    @PreUpdate
    private void normalizeCpf() {
        this.cpf = normalize(this.cpf);
    }

    private String normalize(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF não pode ser nulo");
        }

        String digits = cpf.replaceAll("\\D", "");

        if (digits.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos");
        }

        return digits;
    }
}
