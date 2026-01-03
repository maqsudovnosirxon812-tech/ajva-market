package Ajva.market.repository;

import Ajva.market.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import Ajva.market.entity.CartItem;
import Ajva.market.entity.User;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    List<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);
}
