package br.com.fiap.soat7.infra.config.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaKeyLoader {

    private RsaKeyLoader() {}

    public static PrivateKey loadPrivateKeyFromBase64Env(String base64Env) throws Exception {
        String pem = new String(Base64.getDecoder().decode(base64Env));
        return loadPrivateKeyPkcs8FromPem(pem);
    }

    public static RSAPublicKey loadPublicKeyFromBase64Env(String base64Env) throws Exception {
        String pem = new String(Base64.getDecoder().decode(base64Env));
        return loadPublicKeyX509FromPem(pem);
    }

    private static PrivateKey loadPrivateKeyPkcs8FromPem(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private static RSAPublicKey loadPublicKeyX509FromPem(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(spec);
        return (RSAPublicKey) pub;
    }
}
