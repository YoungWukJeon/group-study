package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "user")
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "salt", nullable = false, length = 50)
    private String salt;
    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
    @Column(name = "last_login_date", nullable = false)
    private LocalDateTime lastLoginDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_no")
    private final List<AuthorityEntity> authorityEntityList = new ArrayList<>();
}
