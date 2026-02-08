package br.com.fiap.soat7.usecase.services.exceptions;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(Long id) {
        super("Carro não encontrado. id=" + id);
    }
}