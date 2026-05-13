package co.vivaeventos.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service", url = "http://localhost:8081")
public interface EventClient {
    
    @PostMapping("/api/v1/events/{eventId}/reserve")
    EventResponse reserveTickets(@PathVariable("eventId") Long eventId, 
                                  @RequestParam("quantity") Integer quantity);
    
    record EventResponse(Long id, String name, Integer availableCapacity, Integer totalCapacity) {}
}