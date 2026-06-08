package co.vivaeventos.orderservice.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_events")
public class PurchaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private Long eventId;

    private String eventName;

    @Column(nullable = false)
    private String step; // STARTED, PAYMENT_INITIATED, PAYMENT_FAILED, PAYMENT_SUCCESS, COMPLETED, ABANDONED

    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String userEmail;

    public PurchaseEvent() {}

    public PurchaseEvent(String sessionId, Long eventId, String eventName, String step, String userEmail) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.step = step;
        this.userEmail = userEmail;
        this.timestamp = LocalDateTime.now();
    }

    public PurchaseEvent(String sessionId, Long eventId, String eventName, String step, String errorMessage, String userEmail) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.step = step;
        this.errorMessage = errorMessage;
        this.userEmail = userEmail;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}