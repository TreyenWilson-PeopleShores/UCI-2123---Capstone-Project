package org.treyenwilson.capstone.eventbooking.mapper;

import org.junit.jupiter.api.Test;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TicketSoldMapperTest {

    private final TicketSoldMapper ticketSoldMapper = new TicketSoldMapper();

    @Test
    void testToResponse() {
        // Arrange
        TicketSold ticketSold = new TicketSold();
        ticketSold.setId(1L);
        ticketSold.setUserId(10L);
        ticketSold.setTicketId(20L);
        ticketSold.setDateSold(LocalDate.of(2026, 4, 24));

        // Act
        TicketSoldResponse response = ticketSoldMapper.toResponse(ticketSold);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getUser_id());
        assertEquals(20L, response.getTicket_id());
        assertEquals(LocalDate.of(2026, 4, 24), response.getDate_sold());
    }

    @Test
    void testToEntity() {
        // Arrange
        TicketSoldRequest request = new TicketSoldRequest();
        request.setUser_id(15L);
        request.setTicket_id(25L);
        request.setDate_sold(LocalDate.of(2026, 4, 25));

        // Act
        TicketSold ticketSold = ticketSoldMapper.toEntity(request);

        // Assert
        assertNotNull(ticketSold);
        assertNull(ticketSold.getId()); // ID should not be set from request
        assertEquals(15L, ticketSold.getUserId());
        assertEquals(25L, ticketSold.getTicketId());
        assertEquals(LocalDate.of(2026, 4, 25), ticketSold.getDateSold());
    }

    @Test
    void testToEntityWithNullRequest() {
        // Arrange
        TicketSoldRequest request = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ticketSoldMapper.toEntity(request));
    }

    @Test
    void testToResponseWithNullTicketSold() {
        // Arrange
        TicketSold ticketSold = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ticketSoldMapper.toResponse(ticketSold));
    }
}