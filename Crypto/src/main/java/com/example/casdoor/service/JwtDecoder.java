package com.example.casdoor.service;

import com.example.casdoor.config.OpenIdConnectProperties;
import com.nimbusds.jose.jwk.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Service
public class JwtDecoder {

    @Autowired
    private OpenIdConnectProperties props;

    public Claims decodeToken(String token) {
        try {
            JWKSet jwkSet = JWKSet.load(new URL(props.getEndpoint() + "/.well-known/jwks"));
            RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Token decode failed", e);
        }
    }
}
