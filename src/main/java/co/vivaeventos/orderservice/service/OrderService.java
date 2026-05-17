package co.vivaeventos.orderservice.service;

import co.vivaeventos.orderservice.client.EventClient;
import co.vivaeventos.orderservice.model.Order;
import co.vivaeventos.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final EventClient eventClient;
    
    @Transactional
    public Order createOrder(Long eventId, Integer quantity, String userEmail, String ticketType, String token) {
        log.info("Creando orden para evento {} tipo {} cantidad {}", eventId, ticketType, quantity);
        
        // Pasar el token en el header
        EventClient.EventResponse event = eventClient.reserveTicketsByType(
            eventId, ticketType, quantity, "Bearer " + token);
        
        Order order = new Order();
        order.setEventId(eventId);
        order.setQuantity(quantity);
        order.setTotalAmount(calculateTotal(quantity));
        order.setStatus("PENDING");
        order.setUserEmail(userEmail);
        
        return orderRepository.save(order);
    }
    
    private Double calculateTotal(Integer quantity) {
        return quantity * 50000.0;
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
    }
    
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con número: " + orderNumber));
    }
    
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}