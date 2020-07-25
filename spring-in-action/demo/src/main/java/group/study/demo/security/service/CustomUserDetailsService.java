package group.study.demo.security.service;

import group.study.demo.persistence.entity.AuthorityEntity;
import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(username);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            List<SimpleGrantedAuthority> authorityList = userEntity.getAuthorityEntityList().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                    .collect(Collectors.toList());

            UserDetails user = User.withUsername(userEntity.getEmail())
                    .password(userEntity.getPassword())
                    .authorities(authorityList).build();
            return user;
        }

        return null;
    }
}
