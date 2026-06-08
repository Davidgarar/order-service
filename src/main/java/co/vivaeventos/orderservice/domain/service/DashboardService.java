package co.vivaeventos.orderservice.domain.service;

import co.vivaeventos.orderservice.domain.model.Ticket;
import co.vivaeventos.orderservice.domain.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Map<String, Object> getSalesDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        List<Ticket> allTickets = ticketRepository.findAll();
        
        // Boletas activas y usadas
        long totalTicketsSold = allTickets.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()) || "USED".equals(t.getStatus()))
                .count();
        
        double totalRevenue = allTickets.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()) || "USED".equals(t.getStatus()))
                .mapToDouble(Ticket::getPrice)
                .sum();
        
        dashboard.put("totalRevenue", totalRevenue);
        dashboard.put("totalTicketsSold", totalTicketsSold);
        
        // Ventas por evento
        Map<Long, Map<String, Object>> salesByEvent = new HashMap<>();
        
        for (Ticket ticket : allTickets) {
            if (!"ACTIVE".equals(ticket.getStatus()) && !"USED".equals(ticket.getStatus())) {
                continue;
            }
            
            salesByEvent.computeIfAbsent(ticket.getEventId(), k -> {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("eventName", ticket.getEventName());
                eventData.put("ticketsSold", 0);
                eventData.put("revenue", 0.0);
                return eventData;
            });
            
            Map<String, Object> eventData = salesByEvent.get(ticket.getEventId());
            eventData.put("ticketsSold", (int) eventData.get("ticketsSold") + 1);
            eventData.put("revenue", (double) eventData.get("revenue") + ticket.getPrice());
        }
        
        dashboard.put("salesByEvent", salesByEvent);
        
        return dashboard;
    }
    
    public Map<String, Object> getSalesByEvent(Long eventId) {
        Map<String, Object> eventSales = new HashMap<>();
        
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        
        long ticketsSold = tickets.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()) || "USED".equals(t.getStatus()))
                .count();
        
        double revenue = tickets.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()) || "USED".equals(t.getStatus()))
                .mapToDouble(Ticket::getPrice)
                .sum();
        
        if (!tickets.isEmpty()) {
            eventSales.put("eventName", tickets.get(0).getEventName());
        }
        eventSales.put("eventId", eventId);
        eventSales.put("ticketsSold", ticketsSold);
        eventSales.put("revenue", revenue);
        
        return eventSales;
    }
    public Map<String, Object> getSalesByHour() {
    Map<String, Object> result = new HashMap<>();
    
    List<Ticket> allTickets = ticketRepository.findAll();
    
    // Ventas por hora del día (0-23)
    Map<Integer, Integer> salesByHour = new HashMap<>();
    Map<Integer, Double> revenueByHour = new HashMap<>();
    
    // Inicializar todas las horas con 0
    for (int i = 0; i < 24; i++) {
        salesByHour.put(i, 0);
        revenueByHour.put(i, 0.0);
    }
    
    for (Ticket ticket : allTickets) {
        if (ticket.getCreatedAt() != null) {
            int hour = ticket.getCreatedAt().getHour();
            salesByHour.put(hour, salesByHour.getOrDefault(hour, 0) + 1);
            revenueByHour.put(hour, revenueByHour.getOrDefault(hour, 0.0) + ticket.getPrice());
        }
    }
    
    // Encontrar la hora con más ventas
    int peakHour = 0;
    int maxSales = 0;
    for (Map.Entry<Integer, Integer> entry : salesByHour.entrySet()) {
        if (entry.getValue() > maxSales) {
            maxSales = entry.getValue();
            peakHour = entry.getKey();
        }
    }
    
    result.put("salesByHour", salesByHour);
    result.put("revenueByHour", revenueByHour);
    result.put("peakHour", peakHour);
    result.put("peakHourSales", maxSales);
    result.put("peakHourRevenue", revenueByHour.get(peakHour));
    
    return result;
}
}