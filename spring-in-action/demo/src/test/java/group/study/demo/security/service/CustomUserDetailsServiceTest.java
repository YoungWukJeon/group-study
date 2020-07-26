package group.study.demo.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_성공() {
        UserDetails user = customUserDetailsService.loadUserByUsername("zzz@a.com");

        assertNotNull(user);
    }
}