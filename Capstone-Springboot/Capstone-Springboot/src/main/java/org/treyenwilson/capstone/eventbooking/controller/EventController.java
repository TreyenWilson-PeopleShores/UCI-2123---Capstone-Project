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
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.repository.EventRepository;
import org.treyenwilson.capstone.eventbooking.service.EventService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

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
    // .../id/{id}/{status: cancelled, completed, or scheduled}
    public ResponseEntity<EventResponse> changeStatus(
            @PathVariable Long id,
            @PathVariable String status)
        {EventResponse response = eventService.changeStatus(id, status);
        return ResponseEntity.ok(response);}

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
        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("eventName".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "event_name";
            } else if ("totalSpots".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "total_spots";
            } else if ("venueId".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "venue_id";
            }
            
            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.filterByDate(start, end, pageable);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.filterByDate(start, end, pageable);
        }
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

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("eventName".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "event_name";
            } else if ("totalSpots".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "total_spots";
            } else if ("venueId".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "venue_id";
            }
            
            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.findAll(pageable);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.findAll(pageable);
        }
    }

    @GetMapping("status/{status}") // .../api/events/1
    // Example call: http://localhost:8080/api/events/status/cancelled?page=3&size=5
    public Page<Event> getByEventStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("eventName".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "event_name";
            } else if ("totalSpots".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "total_spots";
            } else if ("venueId".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "venue_id";
            }
            
            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.findByStatus(pageable, status);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return eventService.findByStatus(pageable, status);
        }
    }

}
