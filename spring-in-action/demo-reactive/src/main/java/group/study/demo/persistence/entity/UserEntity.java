package group.study.demo.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table("user")
public class UserEntity implements Persistable<Long> {
    @Id
    private Long no;
    private String email;
    private String password;
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime lastLoginDate;
    private final List<AuthorityEntity> authorityEntities = new ArrayList<>();

    @Transient
    private boolean newUser;

    @Override
    public Long getId() {
        return no;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.newUser || no == null;
    }

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