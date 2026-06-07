package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cancellations")
public class CancellationController {

    private final NotificationService notificationService;

    public CancellationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> cancelEventAndNotify(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> request) {
        
        String eventName = (String) request.get("eventName");
        
        notificationService.notifyEventCancellation(eventId, eventName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Evento cancelado y notificaciones enviadas");
        response.put("eventId", eventId);
        
        return ResponseEntity.ok(response);
    }
}