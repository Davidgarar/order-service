package co.vivaeventos.orderservice.domain.repository;

import co.vivaeventos.orderservice.domain.model.PurchaseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseEventRepository extends JpaRepository<PurchaseEvent, Long> {
    List<PurchaseEvent> findBySessionId(String sessionId);
    List<PurchaseEvent> findByEventId(Long eventId);
    List<PurchaseEvent> findByStepAndTimestampAfter(String step, LocalDateTime timestamp);
    
    @Query("SELECT p.step, COUNT(p) FROM PurchaseEvent p WHERE p.timestamp > ?1 GROUP BY p.step")
    List<Object[]> countEventsByStep(LocalDateTime since);
}