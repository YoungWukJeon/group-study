package group.study.demo.auth;

import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
        userRepository.save(
                UserEntity.builder()
                        .email("zzz@a.com")
                        .password("testpass")
                        .name("test")
//                        .authorityEntities(Collections.emptyList())
                        .build());

//        Mono<UserDetails> user = authService.findByUsername("zzz@a.com");
//        assertNotNull(user);
    }
}