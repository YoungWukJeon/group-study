package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    @Query("select * from user where email = :email")
    Optional<UserEntity> findByEmail(String email);
}
