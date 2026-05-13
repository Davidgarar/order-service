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
    public Order createOrder(Long eventId, Integer quantity, String userEmail) {
        log.info("Creando orden para evento {} con cantidad {}", eventId, quantity);
        
        // 1. Verificar y reservar boletos en Event Service
        EventClient.EventResponse event = eventClient.reserveTickets(eventId, quantity);
        
        // 2. Crear la orden - usando el constructor con parámetros
        Order order = new Order();
        order.setEventId(eventId);
        order.setQuantity(quantity);
        order.setTotalAmount(calculateTotal(quantity));
        order.setStatus("PENDING");
        order.setUserEmail(userEmail);
        
        // 3. Guardar la orden
        return orderRepository.save(order);
    }
    
    private Double calculateTotal(Integer quantity) {
        // Precio base por boleto: $50,000
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