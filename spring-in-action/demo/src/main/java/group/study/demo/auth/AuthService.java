package group.study.demo.auth;

import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService, ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
//        UserEntity userEntity =
//                userRepository.findByEmail(username)
//                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//
//        List<GrantedAuthority> authorityList = userEntity.getAuthorityEntityList().stream()
//                .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getRole()))
//                .collect(Collectors.toList());
//
//        return User.withUsername(userEntity.getEmail())
//                .password(userEntity.getPassword())
//                .authorities(authorityList)
//                .build();

        return null;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
//        String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
//        // TODO: 2020-08-06 예외 처리
//        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow();
//        userEntity.setLastLoginDate(LocalDateTime.now());
//        userRepository.saveAndFlush(userEntity);
    }
}
