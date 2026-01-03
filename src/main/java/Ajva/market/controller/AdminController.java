package Ajva.market.controller;

import Ajva.market.entity.Order;
import Ajva.market.entity.Product;
import Ajva.market.service.OrderService;
import Ajva.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String adminHome(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders(); // barcha buyurtmalar
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        return "admin-home";
    }

    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/admin";
    }
}
