package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.model.PurchaseEvent;
import co.vivaeventos.orderservice.domain.service.AnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Registrar inicio de compra
    @PostMapping("/track/start")
    public ResponseEntity<?> trackStart(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventName = (String) request.get("eventName");
        String userEmail = (String) request.get("userEmail");
        
        PurchaseEvent event = analyticsService.trackEvent(sessionId, eventId, eventName, "STARTED", userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    // Registrar pago iniciado
    @PostMapping("/track/payment-initiated")
    public ResponseEntity<?> trackPaymentInitiated(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventName = (String) request.get("eventName");
        String userEmail = (String) request.get("userEmail");
        
        PurchaseEvent event = analyticsService.trackEvent(sessionId, eventId, eventName, "PAYMENT_INITIATED", userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    // Registrar pago fallido
    @PostMapping("/track/payment-failed")
    public ResponseEntity<?> trackPaymentFailed(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventName = (String) request.get("eventName");
        String errorMessage = (String) request.get("errorMessage");
        String userEmail = (String) request.get("userEmail");
        
        PurchaseEvent event = analyticsService.trackError(sessionId, eventId, eventName, "PAYMENT_FAILED", errorMessage, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    // Registrar pago exitoso
    @PostMapping("/track/payment-success")
    public ResponseEntity<?> trackPaymentSuccess(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventName = (String) request.get("eventName");
        String userEmail = (String) request.get("userEmail");
        
        PurchaseEvent event = analyticsService.trackEvent(sessionId, eventId, eventName, "PAYMENT_SUCCESS", userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    // Registrar compra completada
    @PostMapping("/track/completed")
    public ResponseEntity<?> trackCompleted(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventName = (String) request.get("eventName");
        String userEmail = (String) request.get("userEmail");
        
        PurchaseEvent event = analyticsService.trackEvent(sessionId, eventId, eventName, "COMPLETED", userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    // Obtener análisis del embudo de compra
    @GetMapping("/funnel")
    public ResponseEntity<Map<String, Object>> getFunnelAnalysis(@RequestParam(defaultValue = "24") Integer hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return ResponseEntity.ok(analyticsService.getFunnelAnalysis(since));
    }

    // Obtener análisis por evento
    @GetMapping("/funnel/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getFunnelByEvent(@PathVariable Long eventId, 
                                                                @RequestParam(defaultValue = "24") Integer hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return ResponseEntity.ok(analyticsService.getFunnelByEvent(eventId, since));
    }

    // Obtener errores más comunes
    @GetMapping("/errors")
    public ResponseEntity<List<PurchaseEvent>> getMostCommonErrors(@RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(analyticsService.getMostCommonErrors(limit));
    }
}