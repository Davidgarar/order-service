package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.model.PurchaseEvent;
import co.vivaeventos.orderservice.domain.repository.PurchaseEventRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final PurchaseEventRepository purchaseEventRepository;

    public AnalyticsService(PurchaseEventRepository purchaseEventRepository) {
        this.purchaseEventRepository = purchaseEventRepository;
    }

    // Registrar un evento de compra
    public PurchaseEvent trackEvent(String sessionId, Long eventId, String eventName, 
                                    String step, String userEmail) {
        PurchaseEvent event = new PurchaseEvent(sessionId, eventId, eventName, step, userEmail);
        return purchaseEventRepository.save(event);
    }

    // Registrar un evento de error
    public PurchaseEvent trackError(String sessionId, Long eventId, String eventName, 
                                    String step, String errorMessage, String userEmail) {
        PurchaseEvent event = new PurchaseEvent(sessionId, eventId, eventName, step, errorMessage, userEmail);
        return purchaseEventRepository.save(event);
    }

    // Obtener análisis de caídas en el embudo de compra
    public Map<String, Object> getFunnelAnalysis(LocalDateTime since) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<Object[]> stepCounts = purchaseEventRepository.countEventsByStep(since);
        
        Map<String, Long> steps = new HashMap<>();
        for (Object[] row : stepCounts) {
            steps.put((String) row[0], (Long) row[1]);
        }
        
        long started = steps.getOrDefault("STARTED", 0L);
        long paymentInitiated = steps.getOrDefault("PAYMENT_INITIATED", 0L);
        long paymentFailed = steps.getOrDefault("PAYMENT_FAILED", 0L);
        long paymentSuccess = steps.getOrDefault("PAYMENT_SUCCESS", 0L);
        long completed = steps.getOrDefault("COMPLETED", 0L);
        long abandoned = steps.getOrDefault("ABANDONED", 0L);
        
        analysis.put("period", since.toString());
        analysis.put("totalStarted", started);
        analysis.put("reachedPayment", paymentInitiated);
        analysis.put("paymentFailed", paymentFailed);
        analysis.put("paymentSuccess", paymentSuccess);
        analysis.put("completed", completed);
        analysis.put("abandoned", abandoned);
        
        // Tasas de conversión
        if (started > 0) {
            analysis.put("conversionToPayment", (double) paymentInitiated / started * 100);
            analysis.put("conversionToSuccess", (double) paymentSuccess / started * 100);
            analysis.put("conversionToCompleted", (double) completed / started * 100);
        }
        
        if (paymentInitiated > 0) {
            analysis.put("paymentFailureRate", (double) paymentFailed / paymentInitiated * 100);
        }
        
        return analysis;
    }

    // Obtener errores más comunes
    public List<PurchaseEvent> getMostCommonErrors(int limit) {
        return purchaseEventRepository.findAll().stream()
                .filter(e -> e.getErrorMessage() != null)
                .limit(limit)
                .toList();
    }

    // Obtener caídas por evento específico
    public Map<String, Object> getFunnelByEvent(Long eventId, LocalDateTime since) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<PurchaseEvent> events = purchaseEventRepository.findByEventId(eventId);
        
        long started = events.stream().filter(e -> "STARTED".equals(e.getStep())).count();
        long paymentFailed = events.stream().filter(e -> "PAYMENT_FAILED".equals(e.getStep())).count();
        long completed = events.stream().filter(e -> "COMPLETED".equals(e.getStep())).count();
        
        analysis.put("eventId", eventId);
        if (!events.isEmpty()) {
            analysis.put("eventName", events.get(0).getEventName());
        }
        analysis.put("started", started);
        analysis.put("paymentFailed", paymentFailed);
        analysis.put("completed", completed);
        
        if (started > 0) {
            analysis.put("failureRate", (double) paymentFailed / started * 100);
        }
        
        return analysis;
    }
}