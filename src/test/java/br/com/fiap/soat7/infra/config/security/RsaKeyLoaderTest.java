package br.com.fiap.soat7.infra.config.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeyLoaderTest {

    @Test
    void loadPrivateKeyPkcs8ShouldLoadKeyFromPemFile() throws Exception {
        Path pem = Path.of("keys/private_key.pem");
        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8(pem);

        assertNotNull(pk);
        assertEquals("RSA", pk.getAlgorithm());
    }

    @Test
    void loadPrivateKeyPkcs8ShouldFailOnInvalidPem() {
        Exception ex = assertThrows(Exception.class,
                () -> RsaKeyLoader.loadPrivateKeyPkcs8(Path.of("keys/public_key.pem")));
        assertNotNull(ex.getMessage());
    }

    @Test
    void loadPrivateKeyPkcs8() {
    }

    @Test
    void loadPublicKeyX509() {
    }
}
