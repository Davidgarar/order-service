package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.model.Ticket;
import co.vivaeventos.orderservice.domain.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String orderId, Long eventId, String eventName, LocalDateTime eventDate,
                               String eventLocation, String ticketType, Double price, String customerName, String customerEmail) {
        Ticket ticket = new Ticket(orderId, eventId, eventName, eventDate, eventLocation, ticketType, price, customerName, customerEmail);
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> getTicketByOrderId(String orderId) {
        return ticketRepository.findByOrderId(orderId);
    }

    public Optional<Ticket> findByQrCode(String qrCode) {
        return ticketRepository.findByQrCode(qrCode);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}