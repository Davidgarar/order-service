package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.model.DiscountCode;
import co.vivaeventos.orderservice.domain.repository.DiscountCodeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {

    private final DiscountCodeRepository discountCodeRepository;

    public DiscountService(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    // Crear código de descuento
    public DiscountCode createDiscountCode(String code, Double percentage, Long eventId, 
                                           Integer maxUses, LocalDateTime validFrom, LocalDateTime validUntil) {
        DiscountCode discountCode = new DiscountCode(code, percentage, eventId, maxUses, validFrom, validUntil);
        return discountCodeRepository.save(discountCode);
    }

    // Validar código
    public Optional<DiscountCode> validateDiscountCode(String code, Long eventId) {
        Optional<DiscountCode> discountOpt = discountCodeRepository.findByCodeAndEventId(code, eventId);
        
        if (discountOpt.isEmpty()) {
            return Optional.empty();
        }
        
        DiscountCode discount = discountOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        if (!discount.getActive() || 
            now.isBefore(discount.getValidFrom()) || 
            now.isAfter(discount.getValidUntil()) ||
            discount.getUsedCount() >= discount.getMaxUses()) {
            return Optional.empty();
        }
        
        return Optional.of(discount);
    }

    // Aplicar descuento a un precio
    public Double applyDiscount(Double originalPrice, Double percentage) {
        return originalPrice * (1 - percentage / 100);
    }

    // Incrementar contador de uso
    public void incrementUsage(DiscountCode discountCode) {
        discountCode.setUsedCount(discountCode.getUsedCount() + 1);
        discountCodeRepository.save(discountCode);
    }

    // Desactivar código
    public void deactivateDiscountCode(Long id) {
        DiscountCode discount = discountCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Código no encontrado"));
        discount.setActive(false);
        discountCodeRepository.save(discount);
    }

    // Listar códigos de un evento
    public List<DiscountCode> getDiscountCodesByEvent(Long eventId) {
        return discountCodeRepository.findByEventId(eventId);
    }
}