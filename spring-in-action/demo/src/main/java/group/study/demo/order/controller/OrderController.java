package group.study.demo.order.controller;

import group.study.demo.order.model.input.OrderInput;
import group.study.demo.order.model.output.OrderOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController {

    @GetMapping
    public String orderView(Model model){
        OrderOutput orderOutput = new OrderOutput();
        orderOutput.setNo(1L);
        orderOutput.setProductName("과자");
        orderOutput.setProductCount(1);
        orderOutput.setUserName("나");
        orderOutput.setOrderDate(LocalDateTime.of(2020, 07, 01, 0, 0, 0));

        model.addAttribute("order", orderOutput);
        return "order";
    }

    @PostMapping("/save")
    public String save(@Valid OrderInput orderInput){
        log.info("input: {}", orderInput);
        return "order";
    }
}
