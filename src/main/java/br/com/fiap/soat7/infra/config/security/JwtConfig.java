package br.com.fiap.soat7.infra.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

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

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${security.jwt.public-key-path}") String publicKeyPath
    ) throws Exception {
        RSAPublicKey pub = RsaKeyLoader.loadPublicKeyX509(Path.of(publicKeyPath));
        return NimbusJwtDecoder.withPublicKey(pub).build();
    }

}
