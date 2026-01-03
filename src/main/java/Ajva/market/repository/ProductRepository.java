package Ajva.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Ajva.market.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
