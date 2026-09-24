package com.aydindemir.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenManager {

    @Value("${authservice.secret.key}")
    private String secretKey;

    @Value("${authservice.issuer}")
    private String issuer;

    @Value("${authservice.expire.date}")
    private Long expireDate;

    public Optional<String> createToken(Long id) {
        try {
            String token = JWT.create()
                    .withClaim("id", id)
                    .withClaim("serviceName", "AuthService")
                    .withIssuer(issuer)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expireDate))
                    .sign(Algorithm.HMAC512(secretKey));

            return Optional.of(token);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public boolean verifyToken(String token) {
        try {
            createVerifier().verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Optional<Long> getIdInfoFromToken(String token) {
        try {
            DecodedJWT decodedJwt = createVerifier().verify(token);
            return Optional.ofNullable(decodedJwt.getClaim("id").asLong());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private JWTVerifier createVerifier() {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }
}
