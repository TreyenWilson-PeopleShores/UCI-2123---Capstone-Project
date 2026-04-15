package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.VenueMapper;
import org.treyenwilson.capstone.eventbooking.repository.VenueRepository;

import java.util.List;

@Service
public class VenueService{
    private final VenueRepository repository;
    private final VenueMapper venueMapper;

    public VenueService(VenueRepository repository, VenueMapper venueMapper) {
        this.repository = repository;
        this.venueMapper = venueMapper;
    }

    public List<Venue> getAllVenues(){
        return  repository.findAll();
    }

    public VenueResponse getByVenueId(Long id) {
        Venue venue = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        return venueMapper.toResponse(venue);
    }

    // Pagination Code
    public Page<Venue> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public Page<Venue> findByLocation(Pageable pageable, String location) {
        return repository.findByLocation(pageable, location);
    }

    public Page<Venue> findByCity(String city, Pageable pageable) {
        return repository.findByCity(city, pageable);
    }

    public Page<Venue> findByState(String state, Pageable pageable) {
        return repository.findByState(state, pageable);
    }

    public VenueResponse createVenue(@Valid VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue saved = repository.save(venue);
        return venueMapper.toResponse(saved);
    }

    public VenueResponse changeLocation(Long id, String location) {
        Venue venue = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        venue.setLocation(location);
        Venue saved = repository.save(venue);
        return venueMapper.toResponse(saved);
    }

}
