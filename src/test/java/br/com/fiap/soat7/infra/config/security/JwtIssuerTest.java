package br.com.fiap.soat7.infra.config.security;

import br.com.fiap.soat7.data.RoleUser;
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
