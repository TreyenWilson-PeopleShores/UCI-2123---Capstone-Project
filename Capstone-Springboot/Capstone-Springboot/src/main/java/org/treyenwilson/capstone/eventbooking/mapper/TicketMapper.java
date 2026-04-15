package org.treyenwilson.capstone.eventbooking.mapper;

import org.springframework.stereotype.Component;
import org.treyenwilson.capstone.eventbooking.dto.TicketRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketResponse;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;

@Component
public class TicketMapper {
    public TicketResponse toResponse(Ticket ticket){
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setEvent_id(ticket.getEvent_id());
        response.setPrice(ticket.getPrice());
        response.setTotal_quantity(ticket.getTotal_quantity());
        response.setSold(ticket.getSold());
        return response;
    }

    public Ticket toEntity(TicketRequest request){
        Ticket ticket = new Ticket();
        ticket.setEvent_id(request.getEvent_id());
        ticket.setPrice(request.getPrice());
        ticket.setTotal_quantity(request.getTotal_quantity());
        ticket.setSold(request.getSold());
        return ticket;
    }
}
