package group.study.demo.auth.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserRegistrationRequest {
    @Email
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;
    @NotEmpty
    private final String passwordConfirm;
    @NotEmpty
    private final String name;
}
