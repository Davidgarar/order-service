package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.model.Ticket;
import co.vivaeventos.orderservice.domain.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Crear boleta (para pruebas)
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            Long eventId = ((Number) request.get("eventId")).longValue();
            String eventName = (String) request.get("eventName");
            LocalDateTime eventDate = LocalDateTime.parse((String) request.get("eventDate"));
            String eventLocation = (String) request.get("eventLocation");
            String ticketType = (String) request.get("ticketType");
            Double price = ((Number) request.get("price")).doubleValue();
            String customerName = (String) request.get("customerName");
            String customerEmail = (String) request.get("customerEmail");

            Ticket ticket = ticketService.createTicket(orderId, eventId, eventName, eventDate, 
                    eventLocation, ticketType, price, customerName, customerEmail);

            return ResponseEntity.status(HttpStatus.CREATED).body(ticket);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear la boleta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Ver detalle de boleta por Order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getTicketByOrderId(@PathVariable String orderId) {
        Optional<Ticket> ticket = ticketService.getTicketByOrderId(orderId);
        
        if (ticket.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No se encontró una boleta para la orden: " + orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(ticket.get());
    }

    // Ver detalle de boleta por QR
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<?> getTicketByQrCode(@PathVariable String qrCode) {
        Optional<Ticket> ticket = ticketService.getTicketByQrCode(qrCode);
        
        if (ticket.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Boleta no válida");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(ticket.get());
    }
}