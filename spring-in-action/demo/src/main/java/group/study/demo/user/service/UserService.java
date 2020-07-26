package group.study.demo.user.service;

import group.study.demo.persistence.entity.AuthorityEntity;
import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import group.study.demo.user.model.request.UserLoginRequest;
import group.study.demo.user.model.request.UserSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public void save(UserSaveRequest userSaveRequest){
        // 유효성 검증
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        LocalDateTime now = LocalDateTime.now();

        AuthorityEntity authorityEntity = AuthorityEntity.builder()
                .role(AuthorityEntity.RoleType.USER.getName())
                .createDate(now)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email(userSaveRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(userSaveRequest.getPassword()))
                .authorityEntityList(Collections.singletonList(authorityEntity))
                .createDate(now)
                .build();

        userRepository.save(userEntity);
    }

}
