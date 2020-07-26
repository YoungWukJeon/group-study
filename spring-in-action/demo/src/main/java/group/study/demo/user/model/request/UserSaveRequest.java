package group.study.demo.user.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserSaveRequest {
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;

    //
}
