package group.study.demo.user.controller;

import group.study.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/join")
//    public String join(){
//        return "join";
//    }
//
//    @PostMapping("/join")
//    public String joinUser(UserSaveRequest userSaveRequest){
//        userService.save(userSaveRequest);
//        return "redirect:/login";
//    }
}
