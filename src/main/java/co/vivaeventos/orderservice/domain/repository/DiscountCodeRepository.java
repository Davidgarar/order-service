package co.vivaeventos.orderservice.domain.repository;

import co.vivaeventos.orderservice.domain.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCodeAndEventId(String code, Long eventId);
    List<DiscountCode> findByEventId(Long eventId);
    List<DiscountCode> findByActiveTrueAndValidUntilAfter(LocalDateTime now);
}