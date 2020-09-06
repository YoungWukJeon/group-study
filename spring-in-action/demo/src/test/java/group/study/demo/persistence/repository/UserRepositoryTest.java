package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.UserEntity;
import io.r2dbc.h2.*;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class UserRepositoryTest {

    @Test
    public void test(){
        H2ConnectionFactory connectionFactory = new H2ConnectionFactory(H2ConnectionConfiguration.builder()
                .inMemory("demo")
                .property(H2ConnectionOption.DB_CLOSE_DELAY, "=-1")
                .build());

        Mono<H2Connection> connection = connectionFactory.create();


        Flux<H2Result> resultFlux = connection
                .block()
                //.createStatement("select * from user where name=$1")
                .createStatement("INSERT INTO user (email, password, name, create_date, update_date, last_login_date)\n" +
                        "VALUES ('test@test.com', '$2a$10$C.Okl5Uo5eWn82/ZKsbWPOf82qox/pC6RzQ9fhhfK.f4MKwaSopbm', '홍길동',\n" +
                        "'2020-05-20 14:01:11', '2020-05-20 14:01:11', '2020-08-04 17:53:38')")
                //.bind("$1", "홍길동")
                .execute();

        resultFlux.subscribe( result -> System.out.println(result) );
    }

//    @Autowired
//    private UserRepository userRepository;

//    @Test
//    void testFindAll() {
//        System.out.println(userRepository.findAll());
//    }
//
//    @Test
//    void testFindById() {
//        System.out.println(userRepository.findById(2L));
//    }
//
//    @Test
//    void testFindByEmail() {
//        Mono<UserEntity> userEntityMono = userRepository.findByEmail("zzz@a.com");
//
////        if (userEntityOptional.isPresent()){
////            System.out.println(userEntityOptional.get());
////            System.out.println(userEntityOptional.get().getAuthorityEntityList());
////        }
//
//        System.out.println(userEntityMono);
//    }
}