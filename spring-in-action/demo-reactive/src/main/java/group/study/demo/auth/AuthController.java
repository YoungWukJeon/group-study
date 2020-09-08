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
        // TODO: 2020-08-09 좀 더 나이스한 valid 처리 및 예외 처리가 필요함
        try {
            if (!userRegistrationRequest.getPassword().equals(userRegistrationRequest.getPasswordConfirm())) {
                return "redirect:/registration";
            }
//            userService.save(userRegistrationRequest);
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/registration";
        }
    }
}
