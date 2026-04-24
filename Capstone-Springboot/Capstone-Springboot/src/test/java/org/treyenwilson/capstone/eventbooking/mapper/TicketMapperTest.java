package org.treyenwilson.capstone.eventbooking.mapper;

import org.junit.jupiter.api.Test;
import org.treyenwilson.capstone.eventbooking.dto.TicketRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketResponse;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;

import static org.junit.jupiter.api.Assertions.*;

class TicketMapperTest {

    private final TicketMapper ticketMapper = new TicketMapper();

    @Test
    void testToResponse() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEvent_id(10L);
        ticket.setPrice(99.99);
        ticket.setTotal_quantity(200L);
        ticket.setSold(50L);

        // Act
        TicketResponse response = ticketMapper.toResponse(ticket);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getEvent_id());
        assertEquals(99.99, response.getPrice());
        assertEquals(200L, response.getTotal_quantity());
        assertEquals(50L, response.getSold());
    }

    @Test
    void testToEntity() {
        // Arrange
        TicketRequest request = new TicketRequest();
        request.setEvent_id(15L);
        request.setPrice(49.99);
        request.setTotal_quantity(100L);
        request.setSold(25L);

        // Act
        Ticket ticket = ticketMapper.toEntity(request);

        // Assert
        assertNotNull(ticket);
        assertNull(ticket.getId()); // ID should not be set from request
        assertEquals(15L, ticket.getEvent_id());
        assertEquals(49.99, ticket.getPrice());
        assertEquals(100L, ticket.getTotal_quantity());
        assertEquals(25L, ticket.getSold());
    }

    @Test
    void testToEntityWithNullRequest() {
        // Arrange
        TicketRequest request = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ticketMapper.toEntity(request));
    }

    @Test
    void testToResponseWithNullTicket() {
        // Arrange
        Ticket ticket = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ticketMapper.toResponse(ticket));
    }
}