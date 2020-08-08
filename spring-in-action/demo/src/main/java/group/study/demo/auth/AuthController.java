package group.study.demo.auth;

import group.study.demo.auth.model.request.UserRegistrationRequest;
import group.study.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/registration")
    public String registration(UserRegistrationRequest userRegistrationRequest) {
        userService.save(userRegistrationRequest);
        return "redirect:/login";
    }
}
