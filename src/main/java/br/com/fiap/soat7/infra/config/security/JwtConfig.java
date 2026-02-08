package br.com.fiap.soat7.infra.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.security.PrivateKey;

@Configuration
public class JwtConfig {

    @Bean
    public JwtIssuer jwtIssuer(
            @Value("${security.jwt.private-key-path}") String privateKeyPath,
            @Value("${security.jwt.issuer}") String issuer
    ) throws Exception {
        PrivateKey pk = RsaKeyLoader.loadPrivateKeyPkcs8(Path.of(privateKeyPath));
        return new JwtIssuer(pk, issuer);
    }
}
