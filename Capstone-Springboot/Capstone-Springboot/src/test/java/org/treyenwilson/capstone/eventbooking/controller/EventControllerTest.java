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
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.service.EventService;

import java.time.LocalDate;
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
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private EventService eventService;
    @Test
    public void testGetEventById_Success() throws Exception {


        
        // Arrange
        Long eventId = 1L;
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(eventId);
        mockResponse.setEvent_name("Test Event");
        mockResponse.setDate(LocalDate.now());
        mockResponse.setStatus("scheduled");
        mockResponse.setTotal_spots(100L);
        mockResponse.setVenue_id(1L);

        when(eventService.getByEventId(eventId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/events/id/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.event_name").value("Test Event"))
                .andExpect(jsonPath("$.status").value("scheduled"));
    }

    @Test
    public void testGetEventById_NotFound() throws Exception {
        // Arrange
        Long eventId = 999L;
        when(eventService.getByEventId(eventId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/events/id/{id}", eventId))
                .andExpect(status().isOk()); // Controller returns 200 even if null
    }

    @Test
    public void testCreateEvent_Success() throws Exception {
        // Arrange
        EventRequest request = new EventRequest();
        request.setEvent_name("New Event");
        request.setDate(LocalDate.now().plusDays(7));
        request.setStatus("scheduled");
        request.setTotal_spots(150L);
        request.setVenue_id(1L);

        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);
        mockResponse.setEvent_name("New Event");
        mockResponse.setDate(request.getDate());
        mockResponse.setStatus("scheduled");
        mockResponse.setTotal_spots(150L);
        mockResponse.setVenue_id(1L);

        when(eventService.createEvent(any(EventRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.event_name").value("New Event"))
                .andExpect(jsonPath("$.status").value("scheduled"));
    }

    @Test
    public void testCreateEvent_ValidationFailure() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        EventRequest request = new EventRequest();
        request.setEvent_name(""); // Empty name should fail validation
        request.setDate(null); // Missing date
        request.setStatus("invalid"); // Invalid status
        request.setTotal_spots(-1L); // Negative spots
        request.setVenue_id(null); // Missing venue id

        // Act & Assert
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllEvents_Success() throws Exception {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        event.setEvent_name("Test Event");
        event.setDate(LocalDate.now());
        event.setStatus("scheduled");
        event.setTotal_spots(100L);
        event.setVenue_id(1L);

        Page<Event> page = new PageImpl<>(List.of(event), PageRequest.of(0, 10), 1);
        when(eventService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].event_name").value("Test Event"));
    }
}