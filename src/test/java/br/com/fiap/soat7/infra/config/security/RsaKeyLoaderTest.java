package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeyLoaderTest {

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    private static String toPemPrivate(KeyPair kp) {
        String base64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" +
                base64.replaceAll("(.{64})", "$1\n") +
                "\n-----END PRIVATE KEY-----";
    }

    @Test
    void loadPrivateKeyPkcs8ShouldLoadKeyFromPemContent() throws Exception {
        KeyPair kp = generateRsaKeyPair();
        String privatePem = toPemPrivate(kp);

        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8FromPem(privatePem);

        assertNotNull(pk);
        assertEquals("RSA", pk.getAlgorithm());
    }
}
