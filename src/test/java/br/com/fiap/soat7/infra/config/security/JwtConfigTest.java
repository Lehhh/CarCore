package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.nio.charset.StandardCharsets;
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
    void jwtIssuerBeanShouldBeCreatedFromPemBase64Content() throws Exception {
        JwtConfig config = new JwtConfig();

        KeyPair kp = generateRsaKeyPair();

        // antes: String privatePem = toPemPrivate(kp);
        String privatePem = toPemPrivate(kp);
        String privatePemB64 = Base64.getEncoder()
                .encodeToString(privatePem.getBytes(StandardCharsets.UTF_8));

        // se seu método agora chama com PRIVATE_KEY_B64 (base64 do PEM inteiro)
        JwtIssuer issuer = config.jwtIssuer(privatePemB64, "carstore");

        assertNotNull(issuer);
    }

    @Test
    void jwtDecoderBeanShouldBeCreatedFromPemBase64Content() throws Exception {
        JwtConfig config = new JwtConfig();

        KeyPair kp = generateRsaKeyPair();

        // antes: String publicPem = toPemPublic(kp);
        String publicPem = toPemPublic(kp);
        String publicPemB64 = Base64.getEncoder()
                .encodeToString(publicPem.getBytes(StandardCharsets.UTF_8));

        JwtDecoder decoder = config.jwtDecoder(publicPemB64);

        assertNotNull(decoder);
    }
}

