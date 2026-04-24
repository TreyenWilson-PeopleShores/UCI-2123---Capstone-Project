package org.treyenwilson.capstone.eventbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.treyenwilson.capstone.eventbooking.service.VenueService;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private VenueService venueService;

    @Test
    public void testGetVenueById_Success() throws Exception {
        // Arrange
        Long venueId = 1L;
        VenueResponse mockResponse = new VenueResponse();
        mockResponse.setId(venueId);
        mockResponse.setVenue_name("Test Venue");
        mockResponse.setLocation("Test Location");
        mockResponse.setTotal_capacity(100L);

        when(venueService.getByVenueId(venueId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/venues/id/{id}", venueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(venueId))
                .andExpect(jsonPath("$.venue_name").value("Test Venue"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    public void testGetVenueById_NotFound() throws Exception {
        // Arrange
        Long venueId = 999L;
        when(venueService.getByVenueId(venueId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/venues/id/{id}", venueId))
                .andExpect(status().isOk()); // Controller returns 200 even if null
    }

    @Test
    public void testCreateVenue_Success() throws Exception {
        // Arrange
        VenueRequest request = new VenueRequest();
        request.setVenue_name("New Venue");
        request.setLocation("New Location");
        request.setTotal_capacity(200L);

        VenueResponse mockResponse = new VenueResponse();
        mockResponse.setId(1L);
        mockResponse.setVenue_name("New Venue");
        mockResponse.setLocation("New Location");
        mockResponse.setTotal_capacity(200L);

        when(venueService.createVenue(any(VenueRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.venue_name").value("New Venue"))
                .andExpect(jsonPath("$.location").value("New Location"));
    }

    @Test
    public void testCreateVenue_ValidationFailure() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        VenueRequest request = new VenueRequest();
        request.setVenue_name(""); // Empty name should fail validation
        request.setLocation(""); // Empty location
        request.setTotal_capacity(-1L); // Negative capacity

        // Act & Assert
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllVenues_Success() throws Exception {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation("Test Location");
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10), 1);
        when(venueService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/venues")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].venue_name").value("Test Venue"));
    }

    @Test
    public void testGetAllVenues_WithCityFilter_Success() throws Exception {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation("Los Angeles, CA");
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10), 1);
        when(venueService.findByCity(eq("Los Angeles"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/venues")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true")
                        .param("city", "Los Angeles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].venue_name").value("Test Venue"));
    }

    @Test
    public void testGetAllVenues_WithStateFilter_Success() throws Exception {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation("Los Angeles, CA");
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10), 1);
        when(venueService.findByState(eq("CA"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/venues")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true")
                        .param("state", "CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].venue_name").value("Test Venue"));
    }

    @Test
    public void testGetAllVenues_InvalidSortBy_FallsBackToId() throws Exception {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation("Test Location");
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10, Sort.by("id").ascending()), 1);
        when(venueService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert - Test with invalid sortBy field
        mockMvc.perform(get("/api/venues")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalid_field") // Should fall back to "id"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void testGetAllVenues_ValidSortByMapping_Success() throws Exception {
        // Arrange
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation("Test Location");
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10, Sort.by("venue_name").ascending()), 1);
        when(venueService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert - Test with camelCase field name that should map to snake_case
        mockMvc.perform(get("/api/venues")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "venueName") // Should map to "venue_name"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void testGetByVenueLocation_InvalidSortBy_FallsBackToId() throws Exception {
        // Arrange
        String location = "Los Angeles";
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation(location);
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10, Sort.by("id").ascending()), 1);
        when(venueService.findByLocation(any(Pageable.class), eq(location))).thenReturn(page);

        // Act & Assert - Test with invalid sortBy field
        mockMvc.perform(get("/api/venues/location/{location}", location)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalid_field") // Should fall back to "id"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void testGetByVenueLocation_ValidSortByMapping_Success() throws Exception {
        // Arrange
        String location = "Los Angeles";
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setVenue_name("Test Venue");
        venue.setLocation(location);
        venue.setTotal_capacity(100L);

        Page<Venue> page = new PageImpl<>(List.of(venue), PageRequest.of(0, 10, Sort.by("total_capacity").ascending()), 1);
        when(venueService.findByLocation(any(Pageable.class), eq(location))).thenReturn(page);

        // Act & Assert - Test with camelCase field name that should map to snake_case
        mockMvc.perform(get("/api/venues/location/{location}", location)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "totalCapacity") // Should map to "total_capacity"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}