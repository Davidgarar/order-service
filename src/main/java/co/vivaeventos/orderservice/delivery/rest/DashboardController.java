package co.vivaeventos.orderservice.delivery.rest;

import co.vivaeventos.orderservice.domain.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesDashboard() {
        return ResponseEntity.ok(dashboardService.getSalesDashboard());
    }

    @GetMapping("/sales/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getSalesByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(dashboardService.getSalesByEvent(eventId));
    }
}