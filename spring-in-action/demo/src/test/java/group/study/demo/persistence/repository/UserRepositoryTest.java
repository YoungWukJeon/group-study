package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindAll() {
        System.out.println(userRepository.findAll());
    }

    @Test
    void testFindById() {
        System.out.println(userRepository.findById(2L));
    }

    @Test
    void testFindByEmail() {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail("zzz@a.com");

        if (userEntityOptional.isPresent()){
            System.out.println(userEntityOptional.get());
            System.out.println(userEntityOptional.get().getAuthorityEntityList());
        }
    }
}