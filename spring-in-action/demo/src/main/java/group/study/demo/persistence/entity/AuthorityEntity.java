package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "authority")
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no", nullable = false)
    private Long no;

    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}
