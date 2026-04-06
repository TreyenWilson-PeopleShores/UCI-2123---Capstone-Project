package org.treyenwilson.capstone.eventbooking.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.service.EventService;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
//    @GetMapping
//    public List<Event> getAllEvents(){
//        return  eventService.getAllEvents();
//    }
    @GetMapping("id/{id}") // .../api/events/1
    public Event getByEventId(@PathVariable Long id){
        return eventService.getByEventId(id);
    }

//    @GetMapping("status/{status}") // .../api/events/1
//    public List<Event> getByEventStatus(@PathVariable String status){
//        return eventService.getByEventStatus(status);
//    } Old method for checking status above



    // Adds Support for pagination below
    @GetMapping
    // Example call: http://localhost:8080/api/events?page=0&size=5&sortBy=date&ascending=false
    public Page<Event> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "31") int size,
            // the default is 31 since the most common amount of days in a month is 31
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
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

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventService.findByStatus(pageable, status);
    }
}
