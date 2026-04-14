package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpLocation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseLocationException;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.VenueMapper;
import org.treyenwilson.capstone.eventbooking.repository.VenueRepository;

import java.time.LocalDate;
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
// old get by venue
//    public Venue getByVenueId(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
//    }

    //new get by venue

    public VenueResponse getByVenueId(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        return venueMapper.toResponse(venue);
    }


    @Autowired
    private VenueRepository venueRepository;

//    public List<Venue> getByVenueLocation(String location) {
//        return venueRepository.findByLocation(location);
//
//    } - old find by location

    // Pagination Code
    public Page<Venue> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }
    public Page<Venue> findByLocation(Pageable pageable, String location) {
        return venueRepository.findByLocation(pageable, location);
    }



//    public Page<Venue> filterByDate(LocalDate start, LocalDate end, Pageable pageable) {
//
//        return venueRepository.findByDateBetween(start, end, pageable);
//    }

//    public Venue save(@Valid Venue newVenue) {
//        return venueRepository.save(newVenue);
//    } - old way of saving


    public VenueResponse createVenue(@Valid VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue saved = venueRepository.save(venue);
        return venueMapper.toResponse(saved);
    }

    public VenueResponse changeLocation(Long id, String location) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(); // to add error exception here
        venue.setLocation(location);
        Venue saved = venueRepository.save(venue);
        return venueMapper.toResponse(saved);
    }

}
