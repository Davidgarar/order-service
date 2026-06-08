package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.model.DiscountCode;
import co.vivaeventos.orderservice.domain.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // Crear código de descuento
    @PostMapping
    public ResponseEntity<?> createDiscountCode(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            Double percentage = ((Number) request.get("percentage")).doubleValue();
            Long eventId = ((Number) request.get("eventId")).longValue();
            Integer maxUses = ((Number) request.get("maxUses")).intValue();
            LocalDateTime validFrom = LocalDateTime.parse((String) request.get("validFrom"));
            LocalDateTime validUntil = LocalDateTime.parse((String) request.get("validUntil"));

            DiscountCode discount = discountService.createDiscountCode(code, percentage, eventId, maxUses, validFrom, validUntil);
            return ResponseEntity.status(HttpStatus.CREATED).body(discount);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear código: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Validar código (para usar en compra)
    @GetMapping("/validate/{code}/{eventId}")
    public ResponseEntity<?> validateDiscountCode(@PathVariable String code, @PathVariable Long eventId) {
        Optional<DiscountCode> discountOpt = discountService.validateDiscountCode(code, eventId);
        
        if (discountOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("message", "Código inválido o expirado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        DiscountCode discount = discountOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("percentage", discount.getPercentage());
        response.put("message", "Código válido - " + discount.getPercentage() + "% de descuento");
        
        return ResponseEntity.ok(response);
    }

    // Listar códigos de un evento
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<DiscountCode>> getDiscountsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(discountService.getDiscountCodesByEvent(eventId));
    }

    // Desactivar código
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateDiscountCode(@PathVariable Long id) {
        discountService.deactivateDiscountCode(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Código desactivado exitosamente");
        return ResponseEntity.ok(response);
    }
}