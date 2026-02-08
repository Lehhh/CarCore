package br.com.fiap.soat7;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class CarStoreBackApplicationTest {

    @Test
    void mainShouldDelegateToSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            CarStoreBackApplication.main(new String[]{"--spring.main.web-application-type=none"});

            mocked.verify(() -> SpringApplication.run(CarStoreBackApplication.class, new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}
