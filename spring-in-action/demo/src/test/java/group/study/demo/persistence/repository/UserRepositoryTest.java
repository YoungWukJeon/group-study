package group.study.demo.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        System.out.println(userRepository.findByEmail("bbb"));
    }

}