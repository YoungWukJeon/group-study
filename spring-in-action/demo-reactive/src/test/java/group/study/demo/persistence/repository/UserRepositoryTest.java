package group.study.demo.persistence.repository;

import group.study.demo.common.config.DatabaseSchemaInitializer;
import group.study.demo.common.config.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class UserRepositoryTest {
    @Autowired
    private DatabaseClient client;
    @Autowired
    private TransactionalOperator rxtx;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void schemaSetUp() {
        DatabaseSchemaInitializer.init();
    }

    @BeforeEach
    public void transactionSetUp() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("group.study.demo.common.config.Transaction").getDeclaredField("rxtx");
        field.setAccessible(true);  // private, static field 접근
        field.set(null, rxtx);
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
        var insertSpecMono = this.client.insert()
                .into("user")
                .value("email", givenEmail)
                .value("password", "testpass")
                .value("name", "홍길동")
                .then();

        insertSpecMono.then(userRepository.findByEmail(givenEmail))
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .consumeNextWith(e -> {
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
        var insertSpecMono = this.client.insert()
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
}