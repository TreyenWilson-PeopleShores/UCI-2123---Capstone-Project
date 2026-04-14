package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.treyenwilson.capstone.eventbooking.repository.VenueRepository;
import org.treyenwilson.capstone.eventbooking.service.VenueService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;

    public VenueController(VenueService venueService, VenueRepository venueRepository) {
        this.venueService = venueService;
    }
//    @GetMapping
//    public List<Venue> getAllVenues(){
//        return  venueService.getAllVenues();
//    }


    @PostMapping() // post to .../venues
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        VenueResponse response = venueService.createVenue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("id/{id}/{location}")
    // Needs to be able to filter by city and state?
    // .../id/{id}/{status: cancelled, completed, or scheduled}
    // Example call: http://localhost:8080/api/venues?page=0&size=5&sortBy=date&ascending=false
    public Page<Venue> getAllVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "31") int size,
            // the default is 31 since the most common amount of days in a month is 31
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "") String state,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size);
        return venueService.findAll(pageable);
    }

//    @GetMapping("status/{status}") // .../api/venues/1
//    public List<Venue> getByVenueStatus(@PathVariable String status){
//        return venueService.getByVenueStatus(status);
//    } Old method for checking status above



    // Adds Support for pagination below
    @GetMapping
    // Example call: http://localhost:8080/api/venues?page=0&size=5&sortBy=date&ascending=false
    public Page<Venue> getAllVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "31") int size,
            // the default is 31 since the most common amount of days in a month is 31
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return venueService.findAll(pageable);
    }

    @GetMapping("status/{status}") // .../api/venues/1
    // Example call: http://localhost:8080/api/venues/status/cancelled?page=3&size=5
    public Page<Venue> getByVenueStatus(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return venueService.findByLocation(pageable, location);//, city, state);
    }

}
