package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.exception.DuplicatePaymentException;
import co.vivaeventos.orderservice.domain.model.Payment;
import co.vivaeventos.orderservice.domain.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            String idempotencyKey = (String) request.get("idempotencyKey");
            Double amount = ((Number) request.get("amount")).doubleValue();
            String paymentMethod = (String) request.get("paymentMethod");

            Payment payment = paymentService.processPayment(orderId, idempotencyKey, amount, paymentMethod);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pago procesado exitosamente");
            response.put("paymentId", payment.getId());
            response.put("status", payment.getStatus());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DuplicatePaymentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("code", "DUPLICATE_PAYMENT");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
}