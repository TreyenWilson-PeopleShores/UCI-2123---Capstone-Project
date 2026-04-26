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
import java.util.Set;

@RestController
@RequestMapping("/api/tickets-sold")
public class TicketSoldController {
    private final TicketSoldService ticketSoldService;
    private final SecurityUtil securityUtil;

    /** Whitelist of field names accepted for ORDER BY to prevent ORDER BY injection. */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "userId", "ticketId", "dateSold");

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

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
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

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ticketSoldService.findByUserId(userId, pageable);
    }

    /**
     * Resolves a user-supplied sort field name to a whitelisted entity field name.
     * Falls back to "id" if the supplied value is not in the allowed set,
     * preventing ORDER BY injection via unvalidated sort parameters.
     */
    private String resolveSortField(String sortBy) {
        String mapped = sortBy;
        if ("user_id".equalsIgnoreCase(sortBy))    mapped = "userId";
        else if ("ticket_id".equalsIgnoreCase(sortBy)) mapped = "ticketId";
        else if ("date_sold".equalsIgnoreCase(sortBy))  mapped = "dateSold";
        return ALLOWED_SORT_FIELDS.contains(mapped) ? mapped : "id";
    }
}