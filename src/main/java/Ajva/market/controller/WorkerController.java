package Ajva.market.controller;

import Ajva.market.entity.Order;
import Ajva.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/worker")
@PreAuthorize("hasRole('ROLE_WORKER')")
public class WorkerController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String workerHome(Model model) {
        List<Order> orders = orderService.getUserOrders("all"); // barcha buyurtmalar
        model.addAttribute("orders", orders);
        return "worker/home";
    }

    @PostMapping("/order/accept")
    public String acceptOrder(@RequestParam Long orderId) {
        orderService.acceptOrder(orderId);
        return "redirect:/worker";
    }

    @PostMapping("/order/reject")
    public String rejectOrder(@RequestParam Long orderId) {
        orderService.rejectOrder(orderId);
        return "redirect:/worker";
    }
}
