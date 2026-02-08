package br.com.fiap.soat7.adapter.controllers;

import br.com.fiap.soat7.data.domain.fake.CarSoldEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/cars")
public class InternalCarSalesController {

    @PostMapping("/{carId}/sold")
    public ResponseEntity<Void> markSold(
            @PathVariable Long carId,
            @RequestBody CarSoldEvent event
    ) {
        // implementação fake: só loga
        System.out.println("CORE recebeu venda do carro " + carId + " -> " + event);

        // aqui você poderia persistir um "audit" ou atualizar uma view interna (opcional)
        return ResponseEntity.noContent().build();
    }
}
