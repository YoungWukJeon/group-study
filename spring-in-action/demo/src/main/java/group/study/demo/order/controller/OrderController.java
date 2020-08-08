package group.study.demo.order.controller;

import group.study.demo.product.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/order")
public class OrderController {
    @GetMapping
    public String orderView(Model model) {
        List<Category> categories = List.of(Category.values());

        return "order";
    }

    @PostMapping(path = "/save")
    public String save() {
        return "order";
    }
}
