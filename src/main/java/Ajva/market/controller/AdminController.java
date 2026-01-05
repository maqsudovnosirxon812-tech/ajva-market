package Ajva.market.controller;

import Ajva.market.entity.Product;
import Ajva.market.entity.User;
import Ajva.market.service.OrderService;
import Ajva.market.service.ProductService;
import Ajva.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Static papkaga yuklash uchun yo'l (production'da tashqi path bo'lgani ma'qul)
    private final String UPLOAD_DIR = "target/classes/static/images/products/";

    @GetMapping
    public String adminHome(Model model) {
        // Asosiy ma'lumotlar
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("orders", orderService.getAllOrders());

        // Ishchilarni filtrlab olish
        List<User> workers = userService.getAllUsers().stream()
                .filter(u -> "ROLE_WORKER".equals(u.getRole()))
                .toList();
        model.addAttribute("workers", workers);

        // Dashboard uchun tezkor statistika
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalWorkers", workers.size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());

        return "admin-home";
    }

    // --- MAHSULOTLAR (OMBOR) NAZORATI ---

    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImage("/images/products/" + fileName);
        }

        productService.saveProduct(product);
        return "redirect:/admin";
    }

    /**
     * ULTRA PRO FEATURE: Mahsulot sonini -99+ boshqarish
     * @param productId Mahsulot IDsi
     * @param action "increase" yoki "decrease"
     */
    @PostMapping("/product/update-stock")
    public String updateStock(@RequestParam Long productId, @RequestParam String action) {
        Product product = productService.findById(productId);
        if (product != null) {
            int currentQty = product.getQuantity();
            if ("increase".equals(action)) {
                product.setQuantity(currentQty + 1);
            } else if ("decrease".equals(action) && currentQty > 0) {
                product.setQuantity(currentQty - 1);
            }
            productService.saveProduct(product);
        }
        return "redirect:/admin#products-section"; // Foydalanuvchi joyini yo'qotmasligi uchun anchor
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    // --- ISHCHILAR (WORKERS) NAZORATI ---

    @PostMapping("/worker/add")
    public String addWorker(@ModelAttribute User worker) {
        worker.setPassword(passwordEncoder.encode(worker.getPassword()));
        worker.setRole("ROLE_WORKER");

        // Default profil rasm
        if (worker.getProfileImage() == null || worker.getProfileImage().isEmpty()) {
            worker.setProfileImage("/images/default-man-profile.png");
        }

        userService.saveUser(worker);
        return "redirect:/admin";
    }

    @PostMapping("/worker/update-salary")
    public String updateSalary(@RequestParam Long workerId, @RequestParam Double salary) {
        User worker = userService.getUserById(workerId);
        if (worker != null) {
            worker.setSalary(salary);
            userService.saveUser(worker);
        }
        return "redirect:/admin";
    }

    // --- ALOQA VA TIZIM ---

    @GetMapping("/chat/{phone}")
    public String openChat(@PathVariable String phone) {
        // Telefon formatini tozalash (masalan: +99890 -> 99890)
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return "redirect:https://t.me/+" + cleanPhone;
    }
}