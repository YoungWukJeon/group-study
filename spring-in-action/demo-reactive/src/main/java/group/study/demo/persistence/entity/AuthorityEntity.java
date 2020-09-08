package group.study.demo.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("authority")
public class AuthorityEntity {
    @Id
    private Long no;
    private Long userNo;
    private String role;
    private LocalDateTime createDate;
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
