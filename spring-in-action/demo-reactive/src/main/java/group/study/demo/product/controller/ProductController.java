package group.study.demo.product.controller;

import group.study.demo.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(path = "/{no}")
    public String productView(@PathVariable("no") Long no,
//                              @AuthenticationPrincipal User user,
                              Model model) {
//        model.addAttribute("user", user);
        model.addAttribute("product", productService.getProductByNo(no));
        return "product";
    }


}