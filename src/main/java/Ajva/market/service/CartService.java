package Ajva.market.service;

import Ajva.market.entity.CartItem;
import Ajva.market.entity.Product;
import Ajva.market.entity.User;
import Ajva.market.repository.CartItemRepository;
import Ajva.market.repository.ProductRepository;
import Ajva.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Savatga mahsulot qo'shish
    @Transactional
    public void addToCart(String username, Long productId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        List<CartItem> items = cartItemRepository.findByUserAndProduct(user, product);
        CartItem item;
        if (items.isEmpty()) {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(1);
        } else {
            item = items.get(0);
            item.setQuantity(item.getQuantity() + 1);
        }

        cartItemRepository.save(item);
    }

    // Savatdagi mahsulotlarni olish
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return cartItemRepository.findByUser(user);
    }

    // Quantity ni oshirish
    @Transactional
    public void increase(String username, Long productId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        List<CartItem> items = cartItemRepository.findByUserAndProduct(user, product);
        if (!items.isEmpty()) {
            CartItem item = items.get(0);
            if (item.getQuantity() < product.getQuantity()) { // ombordagi miqdordan oshmasin
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            }
        }
    }

    // Quantity ni kamaytirish
    @Transactional
    public void decrease(String username, Long productId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        List<CartItem> items = cartItemRepository.findByUserAndProduct(user, product);
        if (!items.isEmpty()) {
            CartItem item = items.get(0);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepository.save(item);
            } else {
                // Quantity 1 bo'lsa, o'chirish
                cartItemRepository.delete(item);
            }
        }
    }

    // Savatdan mahsulotni o'chirish
    @Transactional
    public void remove(String username, Long productId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        cartItemRepository.deleteByUserAndProduct(user, product);
    }

    // Savatdagi jami narx
    @Transactional(readOnly = true)
    public int getTotalPrice(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return cartItemRepository.findByUser(user).stream()
                .mapToInt(i -> (int) (i.getProduct().getPrice() * i.getQuantity()))
                .sum();
    }

    // BUYURTMA BERISH: savat tozalansin va mahsulot quantity kamaysin
    @Transactional
    public void checkout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<CartItem> items = cartItemRepository.findByUser(user);

        for (CartItem item : items) {
            Product product = item.getProduct();

            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException(
                        product.getName() + " mahsulotidan yetarli miqdor yo‘q!"
                );
            }

            // Ombordagi miqdorni kamaytirish
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // Savatni tozalash
        cartItemRepository.deleteAll(items);
    }
    @Transactional
    public void decreaseProductQuantity(Long productId, int amount) {
        Product product = productRepository.findById(productId).orElseThrow();
        int newQuantity = product.getQuantity() - amount;
        product.setQuantity(Math.max(newQuantity, 0)); // quantity 0 dan past bo'lmasin
        productRepository.save(product);
    }

    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<CartItem> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }

}
