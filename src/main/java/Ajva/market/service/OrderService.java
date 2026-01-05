package Ajva.market.service;

import Ajva.market.entity.Order;
import Ajva.market.entity.Product;
import Ajva.market.entity.User;
import Ajva.market.repository.OrderRepository;
import Ajva.market.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi: " + username));
        return orderRepository.findByUser(user);
    }

    public void acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi: " + orderId));
        order.setStatus("accepted");
        orderRepository.save(order);
    }

    public void rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi: " + orderId));
        order.setStatus("rejected");
        orderRepository.save(order);
    }
    // Barcha foydalanuvchilar buyurtmalarini olish
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void createOrder(User user, Product product, int quantity, String address, String phone) {
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus("Yangi"); // default status
        orderRepository.save(order);
    }

    @Transactional
    public Order getOrderById(Long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi: " + orderId));
        return orderRepository.findById(orderId).get();
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
