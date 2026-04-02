package org.treyenwilson.capstone.eventbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Event getByEventId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Test"));
    }
    @Autowired
    private EventRepository eventRepository;
    public List<Event> getByEventStatus(String status) {
        return eventRepository.findByStatus(status);

    }
}
