package org.treyenwilson.capstone.eventbooking.mapper;

import org.springframework.stereotype.Component;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;

@Component
public class TicketSoldMapper {
    public TicketSoldResponse toResponse(TicketSold ticketSold){
        TicketSoldResponse response = new TicketSoldResponse();
        response.setId(ticketSold.getId());
        response.setUser_id(ticketSold.getUserId());
        response.setTicket_id(ticketSold.getTicketId());
        response.setDate_sold(ticketSold.getDateSold());
        return response;
    }

    public TicketSold toEntity(TicketSoldRequest request){
        TicketSold ticketSold = new TicketSold();
        ticketSold.setUserId(request.getUser_id());
        ticketSold.setTicketId(request.getTicket_id());
        ticketSold.setDateSold(request.getDate_sold());
        return ticketSold;
    }
}