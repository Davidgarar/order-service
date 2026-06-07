package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.model.Ticket;
import co.vivaeventos.orderservice.domain.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final TicketRepository ticketRepository;

    public NotificationService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void notifyEventCancellation(Long eventId, String eventName) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        
        if (tickets.isEmpty()) {
            log.info("No hay boletas vendidas para el evento {}", eventId);
            return;
        }
        
        for (Ticket ticket : tickets) {
            // Simular envío de email
            log.info("Enviando notificación a: {} - Cliente: {} - Evento: {} cancelado", 
                ticket.getCustomerEmail(), 
                ticket.getCustomerName(), 
                eventName);
            
            // Marcar boleta como cancelada
            ticket.setStatus("CANCELLED");
            ticketRepository.save(ticket);
        }
        
        log.info("Notificaciones enviadas a {} compradores del evento {}", tickets.size(), eventId);
    }
}