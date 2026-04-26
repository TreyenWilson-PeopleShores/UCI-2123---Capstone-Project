package org.treyenwilson.capstone.eventbooking.mapper;

import org.junit.jupiter.api.Test;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;

import static org.junit.jupiter.api.Assertions.*;

class VenueMapperTest {

    private final VenueMapper venueMapper = new VenueMapper();

    @Test
    void testToResponse() {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Concert Hall");
        venue.setLocation("New York");
        venue.setTotal_capacity(5000L);

        // Act
        VenueResponse response = venueMapper.toResponse(venue);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Concert Hall", response.getVenue_name());
        assertEquals("New York", response.getLocation());
        assertEquals(5000L, response.getTotal_capacity());
    }

    @Test
    void testToEntity() {
        // Arrange
        VenueRequest request = new VenueRequest();
        request.setVenue_name("Stadium");
        request.setLocation("Los Angeles");
        request.setTotal_capacity(10000L);

        // Act
        Venue venue = venueMapper.toEntity(request);

        // Assert
        assertNotNull(venue);
        assertNull(venue.getId()); // ID should not be set from request
        assertEquals("Stadium", venue.getVenue_name());
        assertEquals("Los Angeles", venue.getLocation());
        assertEquals(10000L, venue.getTotal_capacity());
    }

    @Test
    void testToEntityWithNullRequest() {
        // Arrange
        VenueRequest request = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> venueMapper.toEntity(request));
    }

    @Test
    void testToResponseWithNullVenue() {
        // Arrange
        Venue venue = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> venueMapper.toResponse(venue));
    }
}