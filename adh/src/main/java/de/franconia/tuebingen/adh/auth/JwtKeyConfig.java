package de.franconia.tuebingen.adh.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Bean
    RSAPrivateKey jwtPrivateKey(
            @Value("${security.jwt.private-key}")
            Resource resource
    ) throws Exception {

        String pem = resource
                .getContentAsString(StandardCharsets.UTF_8)
                .replace(
                        "-----BEGIN PRIVATE KEY-----",
                        ""
                )
                .replace(
                        "-----END PRIVATE KEY-----",
                        ""
                )
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(pem);

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(decoded);

        return (RSAPrivateKey)
                KeyFactory
                        .getInstance("RSA")
                        .generatePrivate(spec);
    }

    @Bean
    RSAPublicKey jwtPublicKey(
            @Value("${security.jwt.public-key}")
            Resource resource
    ) throws Exception {

        String pem = resource
                .getContentAsString(StandardCharsets.UTF_8)
                .replace(
                        "-----BEGIN PUBLIC KEY-----",
                        ""
                )
                .replace(
                        "-----END PUBLIC KEY-----",
                        ""
                )
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(pem);

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(decoded);

        return (RSAPublicKey)
                KeyFactory
                        .getInstance("RSA")
                        .generatePublic(spec);
    }
}