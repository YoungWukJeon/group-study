package group.study.demo.user.controller;

import group.study.demo.persistence.entity.UserEntity;
import group.study.demo.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/a")
    public String user(Model model) {
        model.addAttribute("user" ,userRepository.findById(1L).orElse(null));
        return "user";
    }

}
