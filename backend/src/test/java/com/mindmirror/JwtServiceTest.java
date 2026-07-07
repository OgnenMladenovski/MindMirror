package com.mindmirror;

import com.mindmirror.config.MindMirrorProperties;
import com.mindmirror.entity.User;
import com.mindmirror.entity.enums.Role;
import com.mindmirror.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService newService() {
        MindMirrorProperties props = new MindMirrorProperties();
        props.getJwt().setSecret("test-secret-that-is-long-enough-1234567890");
        props.getJwt().setExpirationMs(3_600_000);
        return new JwtService(props);
    }

    private User user() {
        User u = new User();
        u.setId(42L);
        u.setUsername("alice");
        u.setRole(Role.STUDENT);
        u.setLocale("mk");
        return u;
    }

    @Test
    void generatesAndParsesToken() {
        JwtService jwt = newService();
        String token = jwt.generateToken(user());
        assertNotNull(token);
        assertEquals("alice", jwt.extractUsername(token));
        assertTrue(jwt.isValid(token));
    }

    @Test
    void rejectsGarbageToken() {
        assertFalse(newService().isValid("not-a-real-token"));
    }
}
