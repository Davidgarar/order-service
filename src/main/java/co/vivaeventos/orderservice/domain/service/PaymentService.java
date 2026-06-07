package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.exception.DuplicatePaymentException;
import co.vivaeventos.orderservice.domain.model.Payment;
import co.vivaeventos.orderservice.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(String orderId, String idempotencyKey, Double amount, String paymentMethod) {
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            Payment p = existing.get();
            if ("COMPLETED".equals(p.getStatus())) {
                throw new DuplicatePaymentException("El pago ya fue procesado exitosamente");
            }
            throw new DuplicatePaymentException("Ya existe un pago en proceso con esta llave");
        }

        Optional<Payment> orderPayment = paymentRepository.findByOrderId(orderId);
        if (orderPayment.isPresent() && "COMPLETED".equals(orderPayment.get().getStatus())) {
            throw new DuplicatePaymentException("La orden " + orderId + " ya fue pagada");
        }

        Payment payment = new Payment(orderId, idempotencyKey, amount, paymentMethod);
        payment.setStatus("COMPLETED");
        return paymentRepository.save(payment);
    }
}