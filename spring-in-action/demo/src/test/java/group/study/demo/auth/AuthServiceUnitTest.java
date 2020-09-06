//package group.study.demo.auth;
//
//import group.study.demo.persistence.entity.AuthorityEntity;
//import group.study.demo.persistence.entity.UserEntity;
//import group.study.demo.persistence.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthServiceUnitTest {
//    @InjectMocks
//    private AuthService authService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    void loadUserByUsername_사용자없음() {
//        final String email = "123@a.com";
//        // given
//        given(userRepository.findByEmail(email))
//                .willReturn(Optional.empty());
//
//        // when
//        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(email));
//
//        // then
//    }
//
//    @Test
//    void loadUserByUsername_성공() {
//        final String email = "123@a.com";
//        List<AuthorityEntity> authorityEntityList = List.of(AuthorityEntity.builder().role("ROLE_USER").userNo(1L).build());
//        // given
//        given(userRepository.findByEmail(email))
//                .willReturn(Optional.of(UserEntity.builder().email(email).password("123").authorityEntityList(authorityEntityList).build()));
//
//        // when
//        UserDetails user = authService.loadUserByUsername(email);
//
//        // then
//        assertEquals(email, user.getUsername());
//        verify(userRepository).findByEmail(email);
//    }
//}
