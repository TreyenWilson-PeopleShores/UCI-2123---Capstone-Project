package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.treyenwilson.capstone.eventbooking.service.VenueService;
import java.util.Set;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;

    /** Whitelist of field names accepted for ORDER BY to prevent ORDER BY injection. */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "venue_name", "location", "total_capacity");

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("id/{id}")
    // Example call: http://localhost:8080/api/venues/1
    public ResponseEntity<VenueResponse> getByVenueId(@PathVariable Long id){
        return ResponseEntity.ok(venueService.getByVenueId(id));
    }

    @PostMapping()
    // Example call: POST http://localhost:8080/api/venues with JSON body
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        VenueResponse response = venueService.createVenue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("id/{id}/{location}")
    // Example call: PUT http://localhost:8080/api/venues/1/Los%20Angeles
    public ResponseEntity<VenueResponse> changeLocation(@PathVariable Long id, @PathVariable String location) {
        VenueResponse response = venueService.changeLocation(id, location);
        return ResponseEntity.ok(response);
    } // Not recommended for use. Use .../venues?city=...state=... Which achieves the same functionality without spaces (%20) inside the URL

    @GetMapping
    // Example call: http://localhost:8080/api/venues?page=0&size=10&sortBy=capacity&ascending=false&city=Los%20Angeles
    public Page<Venue> getAllVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (city != null && !city.isEmpty()) {
            return venueService.findByCity(city, pageable);
        } else if (state != null && !state.isEmpty()) {
            return venueService.findByState(state, pageable);
        } else {
            return venueService.findAll(pageable);
        }
    }

    @GetMapping("location/{location}")
    // Example call: http://localhost:8080/api/venues/location/Los%20Angeles?page=0&size=5&sortBy=name&ascending=true
    public Page<Venue> getByVenueLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return venueService.findByLocation(pageable, location);
    }

    /**
     * Resolves a user-supplied sort field name to a whitelisted entity field name.
     * Falls back to "id" if the supplied value is not in the allowed set,
     * preventing ORDER BY injection via unvalidated sort parameters.
     */
    private String resolveSortField(String sortBy) {
        String mapped = sortBy;
        if ("venueName".equalsIgnoreCase(sortBy))       mapped = "venue_name";
        else if ("totalCapacity".equalsIgnoreCase(sortBy)) mapped = "total_capacity";
        return ALLOWED_SORT_FIELDS.contains(mapped) ? mapped : "id";
    }

}
