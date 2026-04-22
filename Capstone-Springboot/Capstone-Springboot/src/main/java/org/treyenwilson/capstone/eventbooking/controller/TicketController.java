package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.TicketRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketResponse;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;
import org.treyenwilson.capstone.eventbooking.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("id/{id}")
    // Example call: http://localhost:8080/api/tickets/1
    public ResponseEntity<TicketResponse> getByTicketId(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.getByTicketId(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    // Example call: POST http://localhost:8080/api/tickets with JSON body
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("id/{id}/sold/{sold}")
    @PreAuthorize("hasRole('ADMIN')")
    // Example call: PUT http://localhost:8080/api/tickets/1/sold/50
    public ResponseEntity<TicketResponse> updateSoldCount(@PathVariable Long id, @PathVariable Long sold) {
        TicketResponse response = ticketService.updateSoldCount(id, sold);
        return ResponseEntity.ok(response);
    }

    @PutMapping("id/{id}/increment-sold")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // Example call: PUT http://localhost:8080/api/tickets/1/increment-sold
    public ResponseEntity<TicketResponse> incrementSoldCount(@PathVariable Long id) {
        TicketResponse response = ticketService.incrementSoldCount(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Example call: DELETE http://localhost:8080/api/tickets/delete/1
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    } // For admins only

    @GetMapping
    // Example call: http://localhost:8080/api/tickets?page=0&size=10&sortBy=price&ascending=false&minPrice=10.0
    public Page<Ticket> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("eventId".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "event_id";
            } else if ("totalQuantity".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "total_quantity";
            }

            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            if (minPrice != null) {
                return ticketService.findByPriceGreaterThan(minPrice, pageable);
            } else if (maxPrice != null) {
                return ticketService.findByPriceLessThan(maxPrice, pageable);
            } else {
                return ticketService.findAll(pageable);
            }
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            if (minPrice != null) {
                return ticketService.findByPriceGreaterThan(minPrice, pageable);
            } else if (maxPrice != null) {
                return ticketService.findByPriceLessThan(maxPrice, pageable);
            } else {
                return ticketService.findAll(pageable);
            }
        }
    }

    @GetMapping("event/{event_id}")
    // Example call: http://localhost:8080/api/tickets/event/1?page=0&size=5&sortBy=price&ascending=true
    public Page<Ticket> getByEventId(
            @PathVariable Long event_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("eventId".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "event_id";
            } else if ("totalQuantity".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "total_quantity";
            }

            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return ticketService.findByEventId(pageable, event_id);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return ticketService.findByEventId(pageable, event_id);
        }
    }

}
