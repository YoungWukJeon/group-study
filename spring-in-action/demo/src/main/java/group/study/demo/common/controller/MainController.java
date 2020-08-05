package group.study.demo.common.controller;

import group.study.demo.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired
    private ProductService productService;

    @GetMapping(path = {"", "/", "/main"})
    public String main(@RequestParam(name = "category") String category, Model model) {
        model.addAttribute("products", productService.getAllProducts());

        return "main";
    }
}
