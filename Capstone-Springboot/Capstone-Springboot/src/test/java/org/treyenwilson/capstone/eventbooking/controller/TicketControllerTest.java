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
import org.treyenwilson.capstone.eventbooking.dto.TicketRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketResponse;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;
import org.treyenwilson.capstone.eventbooking.service.TicketService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private TicketService ticketService;
    @Test
    public void testGetTicketById_Success() throws Exception {
        // Arrange
        Long ticketId = 1L;
        TicketResponse mockResponse = new TicketResponse();
        mockResponse.setId(ticketId);
        mockResponse.setEvent_id(1L);
        mockResponse.setPrice(50.0);
        mockResponse.setTotal_quantity(100L);
        mockResponse.setSold(10L);

        when(ticketService.getByTicketId(ticketId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/tickets/id/{id}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.event_id").value(1L))
                .andExpect(jsonPath("$.price").value(50.0));
    }

    @Test
    public void testGetTicketById_NotFound() throws Exception {
        // Arrange
        Long ticketId = 999L;
        when(ticketService.getByTicketId(ticketId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/tickets/id/{id}", ticketId))
                .andExpect(status().isOk()); // Controller returns 200 even if null
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateTicket_Success() throws Exception {
        // Arrange
        TicketRequest request = new TicketRequest();
        request.setEvent_id(1L);
        request.setPrice(75.0);
        request.setTotal_quantity(200L);
        request.setSold(0L);

        TicketResponse mockResponse = new TicketResponse();
        mockResponse.setId(1L);
        mockResponse.setEvent_id(1L);
        mockResponse.setPrice(75.0);
        mockResponse.setTotal_quantity(200L);
        mockResponse.setSold(0L);

        when(ticketService.createTicket(any(TicketRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.event_id").value(1L))
                .andExpect(jsonPath("$.price").value(75.0));
    }

    @Test
    public void testCreateTicket_ValidationFailure() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        TicketRequest request = new TicketRequest();
        request.setEvent_id(null); // Missing event_id
        request.setPrice(-10.0); // Negative price
        request.setTotal_quantity(-5L); // Negative quantity
        request.setSold(-1L); // Negative sold

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllTickets_Success() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEvent_id(1L);
        ticket.setPrice(50.0);
        ticket.setTotal_quantity(100L);
        ticket.setSold(10L);

        Page<Ticket> page = new PageImpl<>(List.of(ticket), PageRequest.of(0, 10), 1);
        when(ticketService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].event_id").value(1L))
                .andExpect(jsonPath("$.content[0].price").value(50.0));
    }
}