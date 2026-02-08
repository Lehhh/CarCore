package br.com.fiap.soat7.data.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "car")
@Getter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String brand;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 30)
    private String color;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean sold;

    protected Car() {}

    public Car(String brand, String model, Integer year, String color, BigDecimal price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.price = price;
        this.sold = false;
    }

    public void update(String brand, String model, Integer year, String color, BigDecimal price) {
        if (this.sold) {
            throw new IllegalStateException("Veículo já vendido; não é permitido editar.");
        }
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.price = price;
    }

    public void markAsSold() {
        if (this.sold) {
            throw new IllegalStateException("Veículo já vendido.");
        }
        this.sold = true;
    }
}
