package group.study.demo.product.controller;

import group.study.demo.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping(path = "/test")
    public String productView(Model model) {
        model.addAttribute("product", productRepository.findById(2L).orElse(null));
        return "product";
    }
}
