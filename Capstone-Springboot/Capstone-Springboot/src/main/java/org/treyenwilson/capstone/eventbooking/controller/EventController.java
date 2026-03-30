package org.treyenwilson.capstone.eventbooking.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @GetMapping
    public List<Event> getAllEvents(){
        return  eventService.getAllEvents();
    }
    @GetMapping("/{id}")
    public Event getByEventId(@PathVariable Long id){
        return eventService.getByEventId(id);
    }
}
