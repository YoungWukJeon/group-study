package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    @Query("select * from user where email = :email")
    Mono<UserEntity> findByEmail(String email);
}