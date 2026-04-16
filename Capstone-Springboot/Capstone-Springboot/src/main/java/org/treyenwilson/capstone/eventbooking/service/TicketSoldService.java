package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.TicketSoldMapper;
import org.treyenwilson.capstone.eventbooking.repository.TicketSoldRepository;

@Service
public class TicketSoldService{
    private final TicketSoldRepository repository;
    private final TicketSoldMapper ticketSoldMapper;

    public TicketSoldService(TicketSoldRepository repository, TicketSoldMapper ticketSoldMapper) {
        this.repository = repository;
        this.ticketSoldMapper = ticketSoldMapper;
    }

    public TicketSoldResponse getByTicketSoldId(Long id) {
        TicketSold ticketSold = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketSold", id));
        return ticketSoldMapper.toResponse(ticketSold);
    }

    // Pagination Code
    public Page<TicketSold> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public Page<TicketSold> findByMonth(int month, Pageable pageable) {
        return repository.findByMonth(month, pageable);
    }
    
    public Page<TicketSold> findByYear(int year, Pageable pageable) {
        return repository.findByYear(year, pageable);
    }
    
    public Page<TicketSold> findByMonthAndYear(int month, int year, Pageable pageable) {
        return repository.findByMonthAndYear(month, year, pageable);
    }

    public TicketSoldResponse createTicketSold(@Valid TicketSoldRequest request) {
        TicketSold ticketSold = ticketSoldMapper.toEntity(request);
        TicketSold saved = repository.save(ticketSold);
        return ticketSoldMapper.toResponse(saved);
    }
}