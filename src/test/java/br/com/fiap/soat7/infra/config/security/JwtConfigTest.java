package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtConfigTest {

    private static String toPemPrivate(KeyPair kp) {
        String base64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" +
                base64.replaceAll("(.{64})", "$1\n") +
                "\n-----END PRIVATE KEY-----";
    }

    private static String toPemPublic(KeyPair kp) {
        String base64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                base64.replaceAll("(.{64})", "$1\n") +
                "\n-----END PUBLIC KEY-----";
    }

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    @Test
    void jwtIssuerBeanShouldBeCreatedFromPemContent() throws Exception {
        JwtConfig config = new JwtConfig();

        KeyPair kp = generateRsaKeyPair();
        String privatePem = toPemPrivate(kp);

        JwtIssuer issuer = config.jwtIssuer(privatePem, "carstore");

        assertNotNull(issuer);
    }

    @Test
    void jwtDecoderBeanShouldBeCreatedFromPemContent() throws Exception {
        JwtConfig config = new JwtConfig();

        KeyPair kp = generateRsaKeyPair();
        String publicPem = toPemPublic(kp);

        JwtDecoder decoder = config.jwtDecoder(publicPem);

        assertNotNull(decoder);
    }
}

