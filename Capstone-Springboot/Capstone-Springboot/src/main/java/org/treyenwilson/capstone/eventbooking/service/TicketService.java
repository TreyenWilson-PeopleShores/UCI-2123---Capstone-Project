package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.dto.TicketRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketResponse;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.TicketMapper;
import org.treyenwilson.capstone.eventbooking.repository.TicketRepository;

import java.util.List;

@Service
public class TicketService{
    private final TicketRepository repository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketRepository repository, TicketMapper ticketMapper) {
        this.repository = repository;
        this.ticketMapper = ticketMapper;
    }

    public List<Ticket> getAllTickets(){
        return  repository.findAll();
    }

    public TicketResponse getByTicketId(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        return ticketMapper.toResponse(ticket);
    }

    // Pagination Code
    public Page<Ticket> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Ticket> findByEventId(Pageable pageable, Long event_id) {
        return repository.findByEvent_id(pageable, event_id);
    }

    public Page<Ticket> findByPriceGreaterThan(Double minPrice, Pageable pageable) {
        return repository.findByPriceGreaterThan(minPrice, pageable);
    }

    public Page<Ticket> findByPriceLessThan(Double maxPrice, Pageable pageable) {
        return repository.findByPriceLessThan(maxPrice, pageable);
    }

    public TicketResponse createTicket(@Valid TicketRequest request) {
        Ticket ticket = ticketMapper.toEntity(request);
        Ticket saved = repository.save(ticket);
        return ticketMapper.toResponse(saved);
    }

    public TicketResponse updateSoldCount(Long id, Long sold) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        ticket.setSold(sold);
        Ticket saved = repository.save(ticket);
        return ticketMapper.toResponse(saved);
    }

    public void deleteTicket(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket", id);
        }
        repository.deleteById(id);
    }

}
