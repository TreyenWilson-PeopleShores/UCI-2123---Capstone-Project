package org.treyenwilson.capstone.eventbooking.mapper;

import org.junit.jupiter.api.Test;
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest {

    private final EventMapper eventMapper = new EventMapper();

    @Test
    void testToResponse() {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        event.setEvent_name("Test Event");
        event.setDate(LocalDate.of(2026, 4, 24));
        event.setStatus("scheduled");
        event.setTotal_spots(100L);
        event.setVenue_id(2L);

        // Act
        EventResponse response = eventMapper.toResponse(event);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Event", response.getEvent_name());
        assertEquals(LocalDate.of(2026, 4, 24), response.getDate());
        assertEquals("scheduled", response.getStatus());
        assertEquals(100L, response.getTotal_spots());
        assertEquals(2L, response.getVenue_id());
    }

    @Test
    void testToEntity() {
        // Arrange
        EventRequest request = new EventRequest();
        request.setEvent_name("New Event");
        request.setDate(LocalDate.of(2026, 5, 1));
        request.setStatus("cancelled");
        request.setTotal_spots(150L);
        request.setVenue_id(3L);

        // Act
        Event event = eventMapper.toEntity(request);

        // Assert
        assertNotNull(event);
        assertNull(event.getId()); // ID should not be set from request
        assertEquals("New Event", event.getEvent_name());
        assertEquals(LocalDate.of(2026, 5, 1), event.getDate());
        assertEquals("cancelled", event.getStatus());
        assertEquals(150L, event.getTotal_spots());
        assertEquals(3L, event.getVenue_id());
    }

    @Test
    void testToEntityWithNullRequest() {
        // Arrange
        EventRequest request = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> eventMapper.toEntity(request));
    }

    @Test
    void testToResponseWithNullEvent() {
        // Arrange
        Event event = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> eventMapper.toResponse(event));
    }
}