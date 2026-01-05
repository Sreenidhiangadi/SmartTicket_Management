package com.files.util;

import com.files.model.Role;
import com.files.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET =
            "this-is-a-very-secure-secret-key-for-jwt-tests-12345";

    @Test
    void generateToken_containsExpectedClaims() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        setExpiration(jwtUtil, 3600000);

        User user = User.builder()
                .id("user-id")
                .email("test@test.com")
                .roles(Set.of(Role.USER, Role.ADMIN))
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parse(token);

        assertEquals("user-id", claims.getSubject());
        assertEquals("test@test.com", claims.get("email"));
        assertNotNull(claims.get("roles"));
    }

    @Test
    void generateToken_setsExpiration() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        setExpiration(jwtUtil, 1000);

        User user = User.builder()
                .id("1")
                .email("a@b.com")
                .roles(Set.of(Role.USER))
                .build();

        String token = jwtUtil.generateToken(user);
        Claims claims = parse(token);

        Date expiration = claims.getExpiration();
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void generateToken_setsIssuedAt() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        setExpiration(jwtUtil, 3600000);

        User user = User.builder()
                .id("1")
                .email("a@b.com")
                .roles(Set.of(Role.USER))
                .build();

        String token = jwtUtil.generateToken(user);
        Claims claims = parse(token);

        assertNotNull(claims.getIssuedAt());
    }

    private Claims parse(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void setExpiration(JwtUtil jwtUtil, long value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField("expiration");
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }
}
