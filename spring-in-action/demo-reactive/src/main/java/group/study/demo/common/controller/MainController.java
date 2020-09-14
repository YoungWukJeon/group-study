package group.study.demo.common.controller;

import group.study.demo.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class MainController {
    @Autowired
    private ProductService productService;

    @GetMapping(path = {"", "/", "/main"})
    public String main(@RequestParam(name = "category", required = false) String category,
//                       @AuthenticationPrincipal User user,
                       Model model) {

//        model.addAttribute("user", user);
        // TODO: 2020-08-08 메모리 기반 DB등을 사용해서 인기 상품 목록 불러오기
//        model.addAttribute("popularProducts", productService.getAllProducts());
        // TODO: 2020-08-08 카테고리별로 상품 목록을 가져오도록 Service 변경
//        model.addAttribute("categorizedProducts", productService.getAllProducts());

        Set<String> selectedCategories = category == null?
                Collections.emptySet():
                Arrays.stream(category.split(","))
                        .map(String::trim)
                        .filter(Predicate.not(s -> s.equals("")))
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

        model.addAttribute("selectedCategories", selectedCategories);

        System.out.println("selectedCategories: " + selectedCategories);

//        if (user != null) {
//            System.out.println("**Authentication Information**");
//            System.out.println("Username: " + user.getUsername());
//            System.out.println("Password: " + user.getPassword());
//            System.out.println("Authorities: " + user.getAuthorities());
//            System.out.println("*********************************");
//        }
        return "main";
    }
}