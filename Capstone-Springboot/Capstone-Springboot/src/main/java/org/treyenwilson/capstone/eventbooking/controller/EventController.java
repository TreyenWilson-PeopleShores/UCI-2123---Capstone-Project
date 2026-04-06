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
    @GetMapping("status/{status}") // .../api/events/1
    public List<Event> getByEventStatus(@PathVariable String status){
        return eventService.getByEventStatus(status);
    }

    /*@GetMapping // This is .../api/events?id=1
    public Event getByEventId(@RequestParam Long id){
        return eventService.getByEventId(id);
    }*/


    // Adds Support for pagination below
    @GetMapping
    public Page<Event> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventService.findAll(pageable);
    }
}
