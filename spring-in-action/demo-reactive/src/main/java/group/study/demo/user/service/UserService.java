package group.study.demo.user.service;

import group.study.demo.auth.model.request.UserRegistrationRequest;
import group.study.demo.persistence.entity.AuthorityEntity;
import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public void save(UserRegistrationRequest userRegistrationRequest) {
//        // TODO: 2020-08-06 유효성 검사
//        LocalDateTime now = LocalDateTime.now();
//
//        AuthorityEntity authorityEntity = AuthorityEntity.builder()
//                .role(AuthorityEntity.RoleType.USER.getName())
//                .createDate(now)
//                .updateDate(now)
//                .build();
//
//        UserEntity userEntity = UserEntity.builder()
//                .email(userRegistrationRequest.getEmail())
//                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
//                .name(userRegistrationRequest.getName())
//                .authorityEntities(Collections.singletonList(authorityEntity))
//                .createDate(now)
//                .updateDate(now)
//                .build();
//
//        userRepository.save(userEntity);
//    }
}
