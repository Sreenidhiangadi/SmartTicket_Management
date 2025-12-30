//
//package com.files.util;
//
//import java.time.Instant;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.oauth2.jwt.*;
//import org.springframework.stereotype.Component;
//
//import com.files.model.User;
//import com.nimbusds.jose.jwk.source.ImmutableSecret;
//
//@Component
//public class JwtUtil {
//
//    private final JwtEncoder jwtEncoder;
//
//    public JwtUtil(@Value("${security.jwt.secret}") String secret) {
//        this.jwtEncoder = new NimbusJwtEncoder(
//            new ImmutableSecret<>(secret.getBytes())
//        );
//    }
//
//    @Value("${security.jwt.expiration}")
//    private long expiration;
//
//    public String generateToken(User user) {
//
//        Instant now = Instant.now();
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//            .subject(user.getId())
//            .claim("email", user.getEmail())
//            .claim("roles", user.getRoles())
//            .issuedAt(now)
//            .expiresAt(now.plusMillis(expiration))
//            .build();
//
//        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
//                          .getTokenValue();
//    }
//}
package com.files.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.files.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey key;

    @Value("${security.jwt.expiration}")
    private long expiration;

    public JwtUtil(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {

        return Jwts.builder()
            .setSubject(user.getId())
            .claim("email", user.getEmail())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}

