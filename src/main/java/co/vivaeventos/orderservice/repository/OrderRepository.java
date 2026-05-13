package co.vivaeventos.orderservice.repository;

import co.vivaeventos.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserEmail(String userEmail);
    List<Order> findByEventId(Long eventId);
}