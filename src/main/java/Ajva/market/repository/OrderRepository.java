package Ajva.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Ajva.market.entity.Order;
import Ajva.market.entity.User;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
