package br.com.fiap.soat7.infra.config.security;

import br.com.fiap.soat7.infra.config.security.JwtIssuer;
import br.com.fiap.soat7.infra.config.security.RsaKeyLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Bean
    public JwtIssuer jwtIssuer(
            @Value("${JWT_PRIVATE_KEY}") String privateKeyPem,
            @Value("${JWT_ISSUER}") String issuer
    ) throws Exception {
        // agora lê do CONTEÚDO PEM (secret), não de caminho
        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8FromPem(privateKeyPem);
        return new JwtIssuer(pk, issuer);
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${JWT_PUBLIC_KEY}") String publicKeyPem
    ) throws Exception {
        // corrigido: ${...} e lê do CONTEÚDO PEM (secret)
        RSAPublicKey pub = RsaKeyLoader.loadPublicKeyX509FromPem(publicKeyPem);
        return NimbusJwtDecoder.withPublicKey(pub).build();
    }
}
