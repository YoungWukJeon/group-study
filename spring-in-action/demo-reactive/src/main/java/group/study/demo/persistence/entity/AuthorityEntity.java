package group.study.demo.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "authority")
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public AuthorityEntity(Long userNo, String role, LocalDateTime createDate, LocalDateTime updateDate){
        this.userNo = userNo;
        this.role = role;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public enum RoleType {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        String name;

        RoleType(String name){
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
