package br.com.fiap.soat7.infra.config.security;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtIssuerTest {

    @Test
    void issueShouldGenerateSignedJwtWithExpectedClaims() throws Exception {
        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8(Path.of("keys/private_key.pem"));
        JwtIssuer issuer = new JwtIssuer(pk, "carstore");

        String token = issuer.issue("subj", "user@local", List.of("USER"), 3600L);

        assertNotNull(token);

        SignedJWT parsed = SignedJWT.parse(token);
        var claims = parsed.getJWTClaimsSet();

        assertEquals("carstore", claims.getIssuer());
        assertEquals("subj", claims.getSubject());
        assertEquals("user@local", claims.getStringClaim("email"));
        assertEquals(List.of("USER"), claims.getStringListClaim("roles"));
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertTrue(claims.getExpirationTime().after(claims.getIssueTime()));
    }
}
