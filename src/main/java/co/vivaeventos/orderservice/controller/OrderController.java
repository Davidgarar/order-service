package co.vivaeventos.orderservice.controller;

import co.vivaeventos.orderservice.model.Order;
import co.vivaeventos.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateOrderRequest request) {
        String token = authorization.substring(7); // Quitar "Bearer "
        log.info("Recibida solicitud de orden para evento: {} tipo: {}", request.eventId(), request.ticketType());
        Order order = orderService.createOrder(
            request.eventId(), 
            request.quantity(), 
            request.userEmail(),
            request.ticketType(),
            token
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }
    
    @PutMapping("/{id}/status")  // Cambiado de @PatchMapping a @PutMapping
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusUpdate) {
        Order order = orderService.updateOrderStatus(id, statusUpdate.get("status"));
        return ResponseEntity.ok(order);
    }
    

    public record CreateOrderRequest(Long eventId, Integer quantity, String userEmail, String ticketType) {}
}