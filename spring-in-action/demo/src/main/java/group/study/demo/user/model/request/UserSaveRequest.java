package group.study.demo.user.model.request;

import lombok.Data;

@Data
public class UserSaveRequest {
    private final String email;
    private final String password;
    private final String name;
}
