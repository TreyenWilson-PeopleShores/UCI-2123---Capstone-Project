package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.mapper.EventMapper;
import org.treyenwilson.capstone.eventbooking.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;
    private final EventMapper eventMapper;

    public EventService(EventRepository repository, EventMapper eventMapper) {
        this.repository = repository;
        this.eventMapper = eventMapper;
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

//    public List<Event> getByEventStatus(String status) {
//        return eventRepository.findByStatus(status);
//
//    } - old find by status

    // Pagination Code
    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }
    public Page<Event> findByStatus(Pageable pageable, String status) {
        return eventRepository.findByStatus(pageable, status);
    }



    public Page<Event> filterByDate(LocalDate start, LocalDate end, Pageable pageable) {

        return eventRepository.findByDateBetween(start, end, pageable);
    }

//    public Event save(@Valid Event newEvent) {
//        return eventRepository.save(newEvent);
//    } - old way of saving


    public EventResponse createEvent(@Valid EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }
}
