package org.treyenwilson.capstone.eventbooking;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketSoldTests {

    // ============================================
    // Test Setup and Dependencies
    // ============================================

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // ============================================
    // GET Tests
    // ============================================

    @Transactional
    @Test
    void doesGetFailAtGrabbingATicketSoldIdThatDoesNotExistShouldReturn404() throws Exception {
        // Test: GET request for a non-existent ticket sold ID should return 404 Not Found
        // This tests the error handling when trying to retrieve a ticket sold record that doesn't exist

        mockMvc.perform(get("/api/tickets-sold/id/99999999999999999"))
                .andExpect(status().isNotFound()); // Expect HTTP 404 status

    }



    @Transactional
    @Test
    void isGetAbleToGrabATicketSoldByID() throws Exception {
        // Test: GET request for an existing ticket sold by ID should return 200 OK
        // This tests the successful retrieval of a ticket sold record by its ID

        // Step 1: Create a test ticket sold record in the database
        MockitoAnnotations.openMocks(this);
        TicketSold ticketSold1 = new TicketSold(1L, 3L, 12L, LocalDate.of(2019, 9, 27));
        MockitoAnnotations.openMocks(this);
        TicketSold ticketSold2 = new TicketSold(2L, 4L, 15L, LocalDate.of(2020, 3, 15));

        // Create a TicketSoldRequest object with snake_case field names
        TicketSoldRequest request = new TicketSoldRequest();
        request.setUser_id(3L);
        request.setTicket_id(12L);
        request.setDate_sold(LocalDate.of(2019, 9, 27));

        String json = new ObjectMapper()
                .writeValueAsString(request);
        mockMvc.perform(post("/api/tickets-sold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created for successful POST

        // Step 2: Get all tickets sold to find the ID of the record we just created
        String ticketSoldReturned = mockMvc.perform(get("/api/tickets-sold"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first ticket sold from the paginated response
        int CurrentID =  JsonPath.read(ticketSoldReturned, "$.content[0].id");

        // Step 3: Test GET by ID with the extracted ticket sold ID
        mockMvc.perform(get("/api/tickets-sold/id/"+CurrentID))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful GET

    }


    // ============================================
    // POST Tests
    // ============================================

    @Transactional
    @Test
    void ifPostWorksItReturns201() throws Exception{
        // Test: POST request to create a new ticket sold record should return 201 Created
        // This tests the successful creation of a ticket sold record

        // Create test ticket sold data matching the example from the database
        MockitoAnnotations.openMocks(this);
        TicketSold ticketSold1 = new TicketSold(1L, 3L, 12L, LocalDate.of(2019, 9, 27));
        MockitoAnnotations.openMocks(this);
        TicketSold ticketSold2 = new TicketSold(2L, 4L, 15L, LocalDate.of(2020, 3, 15));

        // Create a TicketSoldRequest object with snake_case field names
        TicketSoldRequest request = new TicketSoldRequest();
        request.setUser_id(3L);
        request.setTicket_id(12L);
        request.setDate_sold(LocalDate.of(2019, 9, 27));

        String json = new ObjectMapper()
                .writeValueAsString(request);
        mockMvc.perform(post("/api/tickets-sold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created for successful POST

    }


    // ============================================
    // Validation Tests
    // ============================================

    @Transactional
    @Test
    void doesPostNotCreateATicketSoldWithMissingDataReturns400() throws Exception{
        // Test: POST request with missing required data should return 400 Bad Request
        // This tests validation when creating a ticket sold record with incomplete data

        mockMvc.perform(post("/api/tickets-sold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"user_id": 3, "ticket_id": 12}
                                """)
                )
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request for missing date_sold field
    }

}