package Ajva.market.controller;

import Ajva.market.entity.Order;
import Ajva.market.entity.User;
import Ajva.market.service.OrderService;
import Ajva.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/worker")
@PreAuthorize("hasRole('ROLE_WORKER')")
public class WorkerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String workerHome(Model model) {
        // XATOLIK TO'G'IRLANDI: "all" o'rniga barcha buyurtmalarni oluvchi metod chaqirildi
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "worker-home";
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

    @PostMapping("/order/complete")
    public String completeOrder(@RequestParam Long orderId, Principal principal) {
        Order order = orderService.getOrderById(orderId);

        // Principal orqali hozirgi login qilgan ishchini topamiz
        User worker = userService.getUserByUsername(principal.getName());

        order.setStatus("DELIVERED"); // Statusni yetkazildi deb o'zgartiramiz
        order.setWorker(worker);

        // Ishchining bajarilgan buyurtmalar sonini oshiramiz
        if (worker.getCompletedOrders() == null) {
            worker.setCompletedOrders(1);
        } else {
            worker.setCompletedOrders(worker.getCompletedOrders() + 1);
        }

        orderService.saveOrder(order);
        userService.saveUser(worker);

        return "redirect:/worker";
    }
}