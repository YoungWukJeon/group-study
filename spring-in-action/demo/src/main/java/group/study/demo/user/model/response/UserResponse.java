package group.study.demo.user.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private final Long userNo;
    private final String email;
    private final String password;
    private final String salt;
    private final String name;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final LocalDateTime lastLoginDate;
}
