package group.study.demo.product.controller;

import group.study.demo.persistence.entity.ProductEntity;
import group.study.demo.persistence.repository.ProductRepository;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/test")
    public String productView(Model model) {
        model.addAttribute("product", productRepository.findById(2L).orElse(null));
        return "product";
    }
}
