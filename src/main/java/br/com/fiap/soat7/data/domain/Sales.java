package br.com.fiap.soat7.data.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(name = "buyer_cpf", nullable = false, length = 11)
    private String buyerCpf;

    @Column(name = "sold_at", nullable = false)
    private LocalDateTime soldAt;

    public Sales(Car car, String buyerCpf, LocalDateTime soldAt) {
        this.car = car;
        this.buyerCpf = buyerCpf;
        this.soldAt = soldAt;
    }
}
