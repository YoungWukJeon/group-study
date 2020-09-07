package group.study.demo.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")})
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_no")
    private final List<AuthorityEntity> authorityEntities = new ArrayList<>();

    @Builder
    public UserEntity(String email, String password, String name, List<AuthorityEntity> authorityEntities,
                      LocalDateTime createDate, LocalDateTime updateDate, LocalDateTime lastLoginDate){
        this.email = email;
        this.password = password;
        this.name = name;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.lastLoginDate = lastLoginDate;
        this.authorityEntities.addAll(authorityEntities);
    }
}
