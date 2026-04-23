package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.repository.EventRepository;
import org.treyenwilson.capstone.eventbooking.service.EventService;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    /** Whitelist of field names accepted for ORDER BY to prevent ORDER BY injection. */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "event_name", "date", "status", "total_spots", "venue_id");

    /** Whitelist of valid event status values. */
    private static final Set<String> ALLOWED_STATUSES =
            Set.of("scheduled", "cancelled", "completed");

    public EventController(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
    }
//    @GetMapping
//    public List<Event> getAllEvents(){
//        return  eventService.getAllEvents();
//    }


    @PostMapping() // post to .../events
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("id/{id}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    // .../id/{id}/{status: cancelled, completed, or scheduled}
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        if (!ALLOWED_STATUSES.contains(status.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body("Invalid status. Allowed values: scheduled, cancelled, completed");
        }
        EventResponse response = eventService.changeStatus(id, status.toLowerCase());
        return ResponseEntity.ok(response);
    }

    @GetMapping("id/{id}") // .../api/events/1
    public ResponseEntity<EventResponse> getByEventId(@PathVariable Long id){
        return ResponseEntity.ok(eventService.getByEventId(id));
    }

//    @GetMapping("status/{status}") // .../api/events/1
//    public List<Event> getByEventStatus(@PathVariable String status){
//        return eventService.getByEventStatus(status);
//    } Old method for checking status above

    @GetMapping("date")
    // Example call: http://localhost:8080/api/events/date?start=2020-01-07&end=2020-02-08
    public Page<Event> filterByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ){
        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventService.filterByDate(start, end, pageable);
    }

    // Adds Support for pagination below
    @GetMapping
    // Example call: http://localhost:8080/api/events?page=0&size=5&sortBy=date&ascending=false
    public Page<Event> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "31") int size,
            // the default is 31 since the most common amount of days in a month is 31
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventService.findAll(pageable);
    }

    @GetMapping("status/{status}") // .../api/events/1
    // Example call: http://localhost:8080/api/events/status/cancelled?page=3&size=5
    public Page<Event> getByEventStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventService.findByStatus(pageable, status);
    }

    /**
     * Resolves a user-supplied sort field name to a whitelisted entity field name.
     * Falls back to "id" if the supplied value is not in the allowed set,
     * preventing ORDER BY injection via unvalidated sort parameters.
     */
    private String resolveSortField(String sortBy) {
        // Normalize camelCase aliases to actual column names
        String mapped = sortBy;
        if ("eventName".equalsIgnoreCase(sortBy))  mapped = "event_name";
        else if ("totalSpots".equalsIgnoreCase(sortBy)) mapped = "total_spots";
        else if ("venueId".equalsIgnoreCase(sortBy))    mapped = "venue_id";
        return ALLOWED_SORT_FIELDS.contains(mapped) ? mapped : "id";
    }

}
