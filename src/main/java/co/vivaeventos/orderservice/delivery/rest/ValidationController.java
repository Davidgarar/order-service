package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.model.Ticket;
import co.vivaeventos.orderservice.domain.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/validation")
public class ValidationController {

    private final TicketService ticketService;

    public ValidationController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/ticket/{qrCode}")
    public ResponseEntity<?> validateTicket(@PathVariable String qrCode) {
        Optional<Ticket> ticketOpt = ticketService.findByQrCode(qrCode);
        
        if (ticketOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Boleta no encontrada o QR inválido");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Ticket ticket = ticketOpt.get();
        Map<String, Object> response = new HashMap<>();
        
        // Si ya está usada
        if ("USED".equals(ticket.getStatus())) {
            response.put("valid", false);
            response.put("message", "Boleta ya fue utilizada");
            return ResponseEntity.ok(response);
        }
        
        // Marcar como usada
        ticket.setStatus("USED");
        ticketService.saveTicket(ticket);
        
        response.put("valid", true);
        response.put("message", "Boleta válida. Puede ingresar.");
        response.put("eventName", ticket.getEventName());
        response.put("eventDate", ticket.getEventDate());
        response.put("ticketType", ticket.getTicketType());
        response.put("customerName", ticket.getCustomerName());
        
        return ResponseEntity.ok(response);
    }
}