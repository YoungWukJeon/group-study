package group.study.demo.auth;

import group.study.demo.persistence.entity.AuthorityEntity;
import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Test
    void findByUsername_사용자없음() {
        final String email = "123@a.com";
        // given
        given(userRepository.findByEmail(email))
                .willReturn(Mono.empty());

        // when
//        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(email));
//        Mono<UserDetails> user = authService.findByUsername(email);

        // then
    }

    @Test
    void findByUsername_성공() {
        final String email = "123@a.com";
        List<AuthorityEntity> authorityEntities = List.of(AuthorityEntity.builder().role("ROLE_USER").userNo(1L).build());
        // given
        given(userRepository.findByEmail(email))
                .willReturn(Mono.just(
                        UserEntity.builder()
                                .email(email)
                                .password("123")
                                .authorityEntities(authorityEntities)
                                .build()));

        // when
//        Mono<UserDetails> user = authService.findByUsername(email);

        // then
//        assertEquals(email, user.getUsername());
//        verify(userRepository).findByEmail(email);
    }
}
