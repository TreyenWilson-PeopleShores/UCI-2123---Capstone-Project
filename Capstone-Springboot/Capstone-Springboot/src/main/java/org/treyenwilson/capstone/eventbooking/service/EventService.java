package org.treyenwilson.capstone.eventbooking.service;

import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.repository.EventRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<Event> getAllEvents(){
        return  repository.findAll();
    }
}
