package Ajva.market.controller;

import Ajva.market.entity.Product;
import Ajva.market.entity.Review;
import Ajva.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("activePage", "home");     // home
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        return "home";
    }
    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id); // Mahsulotni topish
        model.addAttribute("product", product);
        return "product-detail"; // HTML fayl nomi
    }

    @PostMapping("/product/rate")
    @ResponseBody
    public String rateProduct(@RequestParam Long productId, @RequestParam int stars) {
        Product product = productService.findById(productId);

        Review review = new Review();
        review.setStars(stars);
        review.setProduct(product);

        return "OK";
    }

    @GetMapping("/favorites")
    public String favoritesPage(Model model) {
        return "favorites";
    }

}
