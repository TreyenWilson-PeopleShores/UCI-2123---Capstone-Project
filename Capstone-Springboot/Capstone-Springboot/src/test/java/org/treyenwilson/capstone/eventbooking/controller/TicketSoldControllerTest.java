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
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;
import org.treyenwilson.capstone.eventbooking.service.TicketSoldService;
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
public class TicketSoldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private TicketSoldService ticketSoldService;
@TestConfiguration
static class TestConfig {
    @Bean
    EventService eventService() {
        return Mockito.mock(EventService.class);
    }
}
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetTicketSoldById_Success() throws Exception {
        // Arrange
        Long ticketSoldId = 1L;
        TicketSoldResponse mockResponse = new TicketSoldResponse();
        mockResponse.setId(ticketSoldId);
        mockResponse.setUser_id(1L);
        mockResponse.setTicket_id(1L);
        mockResponse.setDate_sold(LocalDate.now());

        when(ticketSoldService.getByTicketSoldId(ticketSoldId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/tickets-sold/id/{id}", ticketSoldId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketSoldId))
                .andExpect(jsonPath("$.user_id").value(1L))
                .andExpect(jsonPath("$.ticket_id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetTicketSoldById_NotFound() throws Exception {
        // Arrange
        Long ticketSoldId = 999L;
        when(ticketSoldService.getByTicketSoldId(ticketSoldId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/tickets-sold/id/{id}", ticketSoldId))
                .andExpect(status().isOk()); // Controller returns 200 even if null
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateTicketSold_Success() throws Exception {
        // Arrange
        TicketSoldRequest request = new TicketSoldRequest();
        request.setUser_id(1L);
        request.setTicket_id(1L);
        request.setDate_sold(LocalDate.now());

        TicketSoldResponse mockResponse = new TicketSoldResponse();
        mockResponse.setId(1L);
        mockResponse.setUser_id(1L);
        mockResponse.setTicket_id(1L);
        mockResponse.setDate_sold(LocalDate.now());

        when(ticketSoldService.createTicketSold(any(TicketSoldRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tickets-sold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user_id").value(1L))
                .andExpect(jsonPath("$.ticket_id").value(1L));
    }

    @Test
    public void testCreateTicketSold_ValidationFailure() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        TicketSoldRequest request = new TicketSoldRequest();
        request.setUser_id(null); // Missing user_id
        request.setTicket_id(null); // Missing ticket_id
        request.setDate_sold(null); // Missing date_sold

        // Act & Assert
        mockMvc.perform(post("/api/tickets-sold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllTicketsSold_Success() throws Exception {
        // Arrange
        TicketSold ticketSold = new TicketSold();
        ticketSold.setId(1L);
        ticketSold.setUserId(1L);
        ticketSold.setTicketId(1L);
        ticketSold.setDateSold(LocalDate.now());

        Page<TicketSold> page = new PageImpl<>(List.of(ticketSold), PageRequest.of(0, 10), 1);
        when(ticketSoldService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/tickets-sold")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].userId").value(1L))
                .andExpect(jsonPath("$.content[0].ticketId").value(1L));
    }
}