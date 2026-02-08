package br.com.fiap.soat7.data.domain;

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

    @Column(nullable = false)
    private String role;

    protected AppUser() {}


    public AppUser(String name, String email, String cpf, String passwordHash, String role) {
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

    public void changeRole(String role) {
        this.role = role;
    }
}
