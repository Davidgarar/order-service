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
    public Order createOrder(Long eventId, Integer quantity, String userEmail, String ticketType, String couponCode, String token) {
        log.info("Creando orden para evento {} tipo {} cantidad {} con cupón: {}", eventId, ticketType, quantity, couponCode);
        
        // Pasar el token en el header
        EventClient.EventResponse event = eventClient.reserveTicketsByType(
            eventId, ticketType, quantity, "Bearer " + token);
        
        Order order = new Order();
        order.setEventId(eventId);
        order.setQuantity(quantity);
        order.setTotalAmount(calculateTotal(quantity, couponCode)); // <-- Se envía el código para calcular el total
        order.setStatus("PENDING");
        order.setUserEmail(userEmail);
        
        // Opcional: Si decides añadir 'couponCode' a tu clase entidad Order.java, puedes descomentar la siguiente línea:
        // order.setCouponCode(couponCode);
        
        return orderRepository.save(order);
    }
    
    // Lógica encargada de interceptar el cálculo base y aplicar la reducción por cupón
    private Double calculateTotal(Integer quantity, String couponCode) {
        double subtotal = quantity * 50000.0;
        double discount = 0.0;
        
        if (couponCode != null && couponCode.equalsIgnoreCase("VIVA2026")) {
            discount = subtotal * 0.10; // 10% de descuento sobre el subtotal
            log.info("¡Cupón VIVA2026 procesado con éxito! Descuento: ${} (Subtotal original: ${})", discount, subtotal);
        }
        
        return subtotal - discount;
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