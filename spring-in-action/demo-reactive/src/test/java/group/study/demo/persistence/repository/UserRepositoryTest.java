package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

// TODO: 2020-09-08 mysql 의존 추가해서 @DataR2dbcTest 해보기
//@DataR2dbcTest
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private DatabaseClient client;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDatabaseClientExisted() {
        assertNotNull(client);
    }

    @Test
    public void testPostRepositoryExisted() {
        assertNotNull(userRepository);
    }

    @Test
    public void testFindByEmail() {
        Mono<UserEntity> userEntityMono = userRepository.findByEmail("zzz@a.com");

        userEntityMono.as(StepVerifier::create)
                .consumeNextWith(p -> assertEquals("zzz@a.com", p.getEmail()))
                .verifyComplete();
//        userEntityMono.subscribe(System.out::println);
    }

    @Test
    void testFindAll() {
        userRepository.findAll().subscribe(System.out::println);
    }

    @Test
    void testFindById() {
        userRepository.findById(2L).subscribe(System.out::println);
    }
}