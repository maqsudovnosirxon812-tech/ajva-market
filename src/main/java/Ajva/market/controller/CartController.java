package Ajva.market.controller;

import Ajva.market.entity.CartItem;
import Ajva.market.service.CartService;
import Ajva.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, Principal principal) {
        cartService.addToCart(principal.getName(), productId);
        return "redirect:/";
    }

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal.getName());
        int totalPrice = cartService.getTotalPrice(principal.getName());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    @GetMapping("/confirm")
    public String showConfirmPage(Model model, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal.getName());

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        int totalPrice = cartService.getTotalPrice(principal.getName());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("defaultPhone", "+998");

        return "checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> checkout(@RequestParam String phone,
                                      @RequestParam String address,
                                      @RequestParam String paymentType,
                                      Principal principal) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(principal.getName());

            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("Savat bo'sh!");
            }

            for (CartItem item : cartItems) {
                orderService.createOrder(
                        item.getUser(),
                        item.getProduct(),
                        item.getQuantity(),
                        address,
                        phone
                );
                cartService.decreaseProductQuantity(item.getProduct().getId(), item.getQuantity());
            }

            cartService.clearCart(principal.getName());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xatolik: " + e.getMessage());
        }
    }

    @PostMapping("/increase")
    public String increase(@RequestParam Long productId, Principal principal) {
        cartService.increase(principal.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/decrease")
    public String decrease(@RequestParam Long productId, Principal principal) {
        cartService.decrease(principal.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId, Principal principal) {
        cartService.remove(principal.getName(), productId);
        return "redirect:/cart";
    }
}