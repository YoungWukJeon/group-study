package group.study.demo.persistence.repository;

import group.study.demo.common.config.R2dbcH2ConfigTest;
import group.study.demo.common.config.Transaction;
import group.study.demo.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class UserRepositoryTest {
    @Autowired
    private DatabaseClient client;
    @Autowired
    private TransactionalOperator rxtx;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("group.study.demo.common.config.Transaction").getDeclaredField("rxtx");
        field.setAccessible(true);  // private, static field 접근
        field.set(null, rxtx);
        R2dbcH2ConfigTest r2dbcH2ConfigTest = new R2dbcH2ConfigTest();
        r2dbcH2ConfigTest.createSchema(client); // 스키마 초기화
    }

    @Test
    public void testDatabaseClientExisted() {
        assertNotNull(client);
    }

    @Test
    public void testUserRepositoryExisted() {
        assertNotNull(userRepository);
    }

    @Test
    public void testFindByEmail() {
        final String givenEmail = "test@test.com";
        UserEntity userEntity = UserEntity.builder()
                .email(givenEmail)
                .password("testpass")
                .name("홍길동")
                .build();
        insertUserEntities(userEntity);

        userRepository.findByEmail(givenEmail)
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(e -> {
                    assertNotNull(e.getNo());
                    assertEquals(givenEmail, e.getEmail());
                    assertEquals("testpass", e.getPassword());
                    assertEquals("홍길동", e.getName());
                })
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        userRepository.findAll()
                .log()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        var insertSpecMono = client.insert()
                .into("user")
                .value("no", 2L)
                .value("email", "test@test.com")
                .value("password", "testpass")
                .value("name", "홍길동")
                .then();

        insertSpecMono.then(userRepository.findById(2L))
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(e -> {
                    assertEquals(2L, e.getNo());
                    assertEquals("test@test.com", e.getEmail());
                    assertEquals("testpass", e.getPassword());
                    assertEquals("홍길동", e.getName());
                })
                .verifyComplete();
    }

    private void insertUserEntities(UserEntity... userEntities) {
        this.userRepository.saveAll(List.of(userEntities))
                .as(StepVerifier::create)
                .expectNextCount(userEntities.length)
                .verifyComplete();
    }
}