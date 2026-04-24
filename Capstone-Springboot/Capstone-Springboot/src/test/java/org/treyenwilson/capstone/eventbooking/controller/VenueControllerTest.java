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
static class TestConfig {
    @Bean
    EventService eventService() {
        return Mockito.mock(EventService.class);
    }
}
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
}