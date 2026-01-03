package Ajva.market.controller;

import Ajva.market.entity.CartItem;
import Ajva.market.service.CartService;
import Ajva.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 1. Mahsulotni savatga qo'shish
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, Principal principal) {
        cartService.addToCart(principal.getName(), productId);
        return "redirect:/";
    }

    // 2. Savat sahifasini ko'rish
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal.getName());
        int totalPrice = cartService.getTotalPrice(principal.getName());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart"; // templates/cart.html
    }

    // 3. Buyurtmani tasdiqlash (Manzil va Telefon kiritish) sahifasi
    // SIZDA SHU METOD YO'QLIGI UCHUN 404 XATOSI CHIQAYOTGAN EDI
    @GetMapping("/confirm")
    public String showConfirmPage(Model model, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal.getName());

        // Agar savat bo'sh bo'lsa, tasdiqlash sahifasiga o'tkazmaymiz
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        int totalPrice = cartService.getTotalPrice(principal.getName());

        model.addAttribute("orders", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("defaultPhone", "+998");
        model.addAttribute("defaultPromo", "");

        return "checkout"; // templates/confirm.html
    }

    // 4. Buyurtmani yakunlash (Bazaga saqlash)
    @PostMapping("/checkout")
    public String checkout(@RequestParam String phone,
                           @RequestParam String address,
                           @RequestParam String paymentType,
                           @RequestParam(required = false) String promoCode,
                           Principal principal) {

        List<CartItem> cartItems = cartService.getCartItems(principal.getName());

        // Har bir mahsulot uchun buyurtma yaratish
        for (CartItem item : cartItems) {
            // Eslatma: orderService.createOrder metodiga address va phone parametrlarini qo'shing
            orderService.createOrder(
                    item.getUser(),
                    item.getProduct(),
                    item.getQuantity(),
                    address,
                    phone
            );

            // Omboradagi sonini kamaytirish
            cartService.decreaseProductQuantity(item.getProduct().getId(), item.getQuantity());
        }

        // Savatni tozalash
        cartService.clearCart(principal.getName());

        // Buyurtmalar ro'yxatiga o'tish
        return "redirect:/orders";
    }

    // 5. Mahsulot sonini oshirish
    @PostMapping("/increase")
    public String increase(@RequestParam Long productId, Principal principal) {
        cartService.increase(principal.getName(), productId);
        return "redirect:/cart";
    }

    // 6. Mahsulot sonini kamaytirish
    @PostMapping("/decrease")
    public String decrease(@RequestParam Long productId, Principal principal) {
        cartService.decrease(principal.getName(), productId);
        return "redirect:/cart";
    }

    // 7. Mahsulotni savatdan o'chirish
    @PostMapping("/remove")
    public String remove(@RequestParam Long productId, Principal principal) {
        cartService.remove(principal.getName(), productId);
        return "redirect:/cart";
    }
}