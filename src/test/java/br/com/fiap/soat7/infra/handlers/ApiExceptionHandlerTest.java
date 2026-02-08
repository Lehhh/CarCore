package br.com.fiap.soat7.infra.handlers;

import br.com.fiap.soat7.usecase.services.exceptions.CarNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    @Test
    void handleNotFoundShouldReturnProblemDetailWithNotFoundStatus() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        var pd = handler.handleNotFound(new CarNotFoundException(10L));

        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("Recurso não encontrado", pd.getTitle());
        assertTrue(pd.getDetail().contains("id=10"));
    }

    @Test
    void handleBusinessShouldReturnProblemDetailWithBadRequestStatus() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        var pd = handler.handleBusiness(new IllegalStateException("x"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Regra de negócio violada", pd.getTitle());
        assertEquals("x", pd.getDetail());
    }
}
