package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    @Test
    void jwtIssuerBeanShouldBeCreatedFromPemPath() throws Exception {
        JwtConfig config = new JwtConfig();

        JwtIssuer issuer = config.jwtIssuer("keys/private_key.pem", "carstore");

        assertNotNull(issuer);
    }
}
