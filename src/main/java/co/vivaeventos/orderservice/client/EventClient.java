package co.vivaeventos.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service", url = "http://localhost:8081")
public interface EventClient {
    
    // Método existente (sin tipo de boleta y sin token)
    @PostMapping("/api/v1/events/{eventId}/reserve")
    EventResponse reserveTickets(@PathVariable("eventId") Long eventId, 
                                  @RequestParam("quantity") Integer quantity);
    
    // NUEVO método con tipo de boleta y token
    @PostMapping("/api/v1/events/{eventId}/ticket-types/{ticketType}/reserve")
    EventResponse reserveTicketsByType(
            @PathVariable("eventId") Long eventId,
            @PathVariable("ticketType") String ticketType,
            @RequestParam("quantity") Integer quantity,
            @RequestHeader("Authorization") String authorization);
    
    record EventResponse(Long id, String name, Integer availableCapacity, Integer totalCapacity) {}
}