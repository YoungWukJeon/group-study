package group.study.demo.user.model.request;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginRequest {
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;
}
