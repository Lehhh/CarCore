package br.com.fiap.soat7.usecase.services.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handle(IllegalArgumentException ex) {

        String message = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Unauthorized"
                : ex.getMessage();

        return ResponseEntity
                .status(401)
                .body(Map.of("message", message));
    }
}