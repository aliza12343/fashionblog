package org.example.capstone2;

import org.example.capstone2.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("fashionblog-secret-key-must-be-at-least-256-bits!!");
    }

    @Test
    void generateToken_ShouldReturnNonBlankToken() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_ShouldContainThreeParts() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("alice", "ROLE_USER");
        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForGarbageToken() {
        assertFalse(jwtUtil.validateToken("not.a.jwt"));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForEmptyString() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForTamperedToken() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void tokensForDifferentUsers_ShouldBeDifferent() {
        String token1 = jwtUtil.generateToken("alice", "ROLE_USER");
        String token2 = jwtUtil.generateToken("bob", "ROLE_ADMIN");
        assertNotEquals(token1, token2);
    }
}
