package br.com.fiap.soat7.infra.config.security;

import br.com.fiap.soat7.data.RoleUser;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtIssuerTest {

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    private static String toPemPrivate(PrivateKey privateKey) {
        String base64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" +
                base64.replaceAll("(.{64})", "$1\n") +
                "\n-----END PRIVATE KEY-----";
    }

    @Test
    void issueShouldGenerateSignedJwtWithExpectedClaims() throws Exception {
        KeyPair kp = generateRsaKeyPair();

        String privatePem = toPemPrivate(kp.getPrivate());
        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8FromPem(privatePem);

        JwtIssuer issuer = new JwtIssuer(pk, "carstore");

        String token = issuer.issue("subj", "user@local", RoleUser.ROLE_ADMIN, 3600L);

        assertNotNull(token);

        SignedJWT parsed = SignedJWT.parse(token);
        var claims = parsed.getJWTClaimsSet();

        assertEquals("carstore", claims.getIssuer());
        assertEquals("subj", claims.getSubject());
        assertEquals("user@local", claims.getStringClaim("email"));
        assertEquals("ROLE_ADMIN", claims.getStringClaim("role"));
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertTrue(claims.getExpirationTime().after(claims.getIssueTime()));
    }
}

