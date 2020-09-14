package group.study.demo.auth;

import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
//public class AuthService implements UserDetailsService, ApplicationListener<AuthenticationSuccessEvent> {
//public class AuthService implements ReactiveUserDetailsService {
public class AuthService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public Mono<UserDetails> findByUsername(String username) {
//        return userRepository.findByEmail(username)
//                .map(e -> User.withUsername(e.getEmail())
//                        .password(e.getPassword())
//                        .authorities(
//                                e.getAuthorityEntities()
//                                        .stream()
//                                        .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getRole()))
//                                        .collect(Collectors.toList()))
//                        .build());
//    }
}

//    @Override
//    public Mono<UserDetails> loadUserByUsername(String username) {
//        return userRepository.findByEmail(username)
////                        .map(e -> e.getAuthorityEntities()
////                                .stream()
////                                .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getRole())))
//                .map(e ->
//                    User.withUsername(e.getEmail())
//                            .password(e.getPassword())
//                            .authorities(
//                                    e.getAuthorityEntities()
//                                            .stream()
//                                            .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getRole()))
//                                            .collect(Collectors.toList()))
//
//                            .build()
//        );
////                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//
////        List<GrantedAuthority> authorityList = userEntity.getAuthorityEntities().stream()
////                .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getRole()))
////                .collect(Collectors.toList());
////
////        return User.withUsername(userEntity.getEmail())
////                .password(userEntity.getPassword())
////                .authorities(authorityList)
////                .build();
//    }
//
//    @Override
//    public void onApplicationEvent(AuthenticationSuccessEvent event) {
//        String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
//        // TODO: 2020-08-06 예외 처리
//        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow();
//        userEntity.setLastLoginDate(LocalDateTime.now());
////        userRepository.saveAndFlush(userEntity);
//        userRepository.saveAndFlush(userEntity);
//    }
