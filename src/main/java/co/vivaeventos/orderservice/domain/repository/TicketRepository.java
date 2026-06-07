package co.vivaeventos.orderservice.domain.repository;

import co.vivaeventos.orderservice.domain.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByOrderId(String orderId);
    Optional<Ticket> findByQrCode(String qrCode);
}