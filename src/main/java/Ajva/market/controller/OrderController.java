package Ajva.market.controller;

import Ajva.market.entity.Order;
import Ajva.market.service.OrderService;
import Ajva.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) {
        List<Order> orders = orderService.getUserOrders(principal.getName());
        model.addAttribute("orders", orders);

        model.addAttribute("defaultPhone", userService.getUserByUsername(principal.getName()).getPhone());
        model.addAttribute("defaultPromo", "ajva2026");

        return "checkout";
    }
}
