package group.study.demo.auth;

import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import javax.transaction.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_성공() {
        userRepository.saveAndFlush(
                UserEntity.builder()
                        .email("zzz@a.com")
                        .password("testpass")
                        .name("test")
                        .authorityEntityList(Collections.emptyList())
                        .build());

        UserDetails user = authService.loadUserByUsername("zzz@a.com");
        assertNotNull(user);
    }
}