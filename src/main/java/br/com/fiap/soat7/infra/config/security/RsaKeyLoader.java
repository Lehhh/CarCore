package br.com.fiap.soat7.infra.config.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaKeyLoader {

    private RsaKeyLoader() {}

    public static PrivateKey loadPrivateKeyFromBase64Env(String pemFileB64) throws Exception {
        String pem = new String(Base64.getDecoder().decode(pemFileB64.trim()), StandardCharsets.UTF_8)
                .replace("\r", "")
                .trim();

        // PKCS#8
        if (pem.contains("BEGIN PRIVATE KEY")) {
            return loadPrivateKeyPkcs8FromPem(pem);
        }

        // PKCS#1 -> converte para PKCS#8
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            return loadPrivateKeyPkcs1FromPem(pem);
        }

        throw new IllegalArgumentException("Formato de chave privada não suportado. Esperado PEM PKCS#8 ou PKCS#1.");
    }

    public static RSAPublicKey loadPublicKeyFromBase64Env(String pemFileB64) throws Exception {
        String pem = new String(Base64.getDecoder().decode(pemFileB64.trim()), StandardCharsets.UTF_8)
                .replace("\r", "")
                .trim();
        return loadPublicKeyX509FromPem(pem);
    }

    private static PrivateKey loadPrivateKeyPkcs8FromPem(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    // PKCS#1 (BEGIN RSA PRIVATE KEY) -> transforma em PKCS#8 (sem libs externas)
    private static PrivateKey loadPrivateKeyPkcs1FromPem(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] pkcs1 = Base64.getDecoder().decode(content);

        // wrap DER PKCS#1 em estrutura PKCS#8:
        // SEQUENCE( version=0, algorithm=RSA OID, OCTET STRING(pkcs1) )
        byte[] pkcs8 = Pkcs8Util.wrapRsaPkcs1ToPkcs8(pkcs1);

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(pkcs8));
    }

    private static RSAPublicKey loadPublicKeyX509FromPem(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        return (RSAPublicKey) pub;
    }

    // util interno
    static final class Pkcs8Util {
        private Pkcs8Util() {}

        static byte[] wrapRsaPkcs1ToPkcs8(byte[] pkcs1) {
            // DER prefix fixo para RSA (OID 1.2.840.113549.1.1.1) com NULL params
            // + OCTET STRING contendo o PKCS#1
            byte[] algId = new byte[] {
                    0x30, 0x0D,
                    0x06, 0x09, 0x2A, (byte)0x86, 0x48, (byte)0x86, (byte)0xF7, 0x0D, 0x01, 0x01, 0x01,
                    0x05, 0x00
            };

            byte[] version = new byte[] { 0x02, 0x01, 0x00 };

            byte[] octetString = derOctetString(pkcs1);

            byte[] seq = derSequence(concat(version, algId, octetString));
            return seq;
        }

        private static byte[] derOctetString(byte[] data) {
            return concat(new byte[] { 0x04 }, derLength(data.length), data);
        }

        private static byte[] derSequence(byte[] data) {
            return concat(new byte[] { 0x30 }, derLength(data.length), data);
        }

        private static byte[] derLength(int len) {
            if (len < 128) return new byte[] { (byte) len };
            if (len < 256) return new byte[] { (byte) 0x81, (byte) len };
            return new byte[] { (byte) 0x82, (byte) (len >> 8), (byte) (len & 0xFF) };
        }

        private static byte[] concat(byte[]... parts) {
            int total = 0;
            for (byte[] p : parts) total += p.length;
            byte[] out = new byte[total];
            int pos = 0;
            for (byte[] p : parts) {
                System.arraycopy(p, 0, out, pos, p.length);
                pos += p.length;
            }
            return out;
        }
    }
}
