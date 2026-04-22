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
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.security.SecurityUtil;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;
import org.treyenwilson.capstone.eventbooking.service.TicketSoldService;

@RestController
@RequestMapping("/api/tickets-sold")
public class TicketSoldController {
    private final TicketSoldService ticketSoldService;
    private final SecurityUtil securityUtil;

    public TicketSoldController(TicketSoldService ticketSoldService, SecurityUtil securityUtil) {
        this.ticketSoldService = ticketSoldService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Example call: http://localhost:8080/api/tickets-sold/id/1
    public ResponseEntity<TicketSoldResponse> getByTicketSoldId(@PathVariable Long id){
        return ResponseEntity.ok(ticketSoldService.getByTicketSoldId(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // Example call: POST http://localhost:8080/api/tickets-sold with JSON body
    public ResponseEntity<TicketSoldResponse> createTicketSold(@Valid @RequestBody TicketSoldRequest request) {
        TicketSoldResponse response = ticketSoldService.createTicketSold(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Example calls: 
    // http://localhost:8080/api/tickets-sold?page=0&size=10&sortBy=dateSold&ascending=true
    // http://localhost:8080/api/tickets-sold?month=4 (April tickets)
    // http://localhost:8080/api/tickets-sold?year=2020 (2020 tickets)
    // http://localhost:8080/api/tickets-sold?month=4&year=2020 (April 2020 tickets)
    public Page<TicketSold> getAllTicketsSold(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("user_id".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "userId";
            } else if ("ticket_id".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "ticketId";
            } else if ("date_sold".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "dateSold";
            }

            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Handle search by month and/or year
            if (month != null && year != null) {
                return ticketSoldService.findByMonthAndYear(month, year, pageable);
            } else if (month != null) {
                return ticketSoldService.findByMonth(month, pageable);
            } else if (year != null) {
                return ticketSoldService.findByYear(year, pageable);
            } else {
                return ticketSoldService.findAll(pageable);
            }
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return ticketSoldService.findAll(pageable);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtil.isCurrentUser(#userId)")
    // Example call: http://localhost:8080/api/tickets-sold/user/3?page=0&size=10&sortBy=dateSold&ascending=true
    public Page<TicketSold> getTicketsSoldByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("user_id".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "userId";
            } else if ("ticket_id".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "ticketId";
            } else if ("date_sold".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "dateSold";
            }

            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            return ticketSoldService.findByUserId(userId, pageable);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return ticketSoldService.findByUserId(userId, pageable);
        }
    }
}