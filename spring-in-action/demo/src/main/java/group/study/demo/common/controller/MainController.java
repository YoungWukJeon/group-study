package group.study.demo.common.controller;

import group.study.demo.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired
    private ProductService productService;

    @GetMapping(path = {"", "/", "/main"})
    public String main(@RequestParam(name = "category", required = false) String category,
                       @AuthenticationPrincipal User user,
                       Model model) {
        model.addAttribute("products", productService.getAllProducts());
        if (user != null) {
            System.out.println("**Authentication Information**");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Authorities: " + user.getAuthorities());
            System.out.println("*********************************");
        }

        return "main";
    }
}
