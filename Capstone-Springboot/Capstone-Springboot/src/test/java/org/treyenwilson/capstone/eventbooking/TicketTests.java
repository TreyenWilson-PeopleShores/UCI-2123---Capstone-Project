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
import org.treyenwilson.capstone.eventbooking.entity.Ticket;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketTests {

    // ============================================
    // Test Setup and Dependencies
    // ============================================

    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;


    // ============================================
    // GET Tests
    // ============================================

    @Transactional
    @Test
    void doesGetFailAtGrabbingATicketIdThatDoesNotExistShouldReturn404() throws Exception {
        // Test: GET request for a non-existent ticket ID should return 404 Not Found
        // This tests the error handling when trying to retrieve a ticket that doesn't exist

        mockMvc.perform(get("/api/tickets/id/99999999999999999"))
                .andExpect(status().isNotFound()); // Expect HTTP 404 status

    }



    @Transactional
    @Test
    void isGetAbleToGrabATicketByID() throws Exception {
        // Test: GET request for an existing ticket by ID should return 200 OK
        // This tests the successful retrieval of a ticket by its ID

        // Step 1: Create a test ticket in the database
        MockitoAnnotations.openMocks(this);
        Ticket ticket1 = new Ticket(1L, 1L, 48.86, 16696L, 15437L);
        MockitoAnnotations.openMocks(this);
        Ticket ticket2 = new Ticket(2L, 2L, 99.99, 5000L, 2500L);

        String json = new ObjectMapper()
                .writeValueAsString(ticket1);
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created for successful POST

        // Step 2: Get all tickets to find the ID of the ticket we just created
        String ticketReturned = mockMvc.perform(get("/api/tickets"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first ticket from the paginated response
        int CurrentID =  JsonPath.read(ticketReturned, "$.content[0].id");

        // Step 3: Test GET by ID with the extracted ticket ID
        mockMvc.perform(get("/api/tickets/id/"+CurrentID))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful GET

    }


    // ============================================
    // POST Tests
    // ============================================

    @Transactional
    @Test
    void ifPostWorksItReturns201() throws Exception{
        // Test: POST request to create a new ticket should return 201 Created
        // This tests the successful creation of a ticket

        // Create test ticket data
        MockitoAnnotations.openMocks(this);
        Ticket ticket1 = new Ticket(1L, 1L, 48.86, 16696L, 15437L);
        MockitoAnnotations.openMocks(this);
        Ticket ticket2 = new Ticket(2L, 2L, 99.99, 5000L, 2500L);

        // Convert ticket object to JSON
        String json = new ObjectMapper()
                .writeValueAsString(ticket1);

        // Send POST request to create ticket
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created
    }


    // ============================================
    // PUT Tests
    // ============================================

    // Test: PUT request to update ticket sold count should return 200 OK
    @Transactional
    @Test
    void isPutAbleToModifyATicketShouldReturn200() throws Exception {
        // This tests the successful update of a ticket's sold count

        // Step 1: Create a test ticket in the database
        Ticket ticket1 = new Ticket(1L, 1L, 48.86, 16696L, 15437L);
        Ticket ticket2 = new Ticket(2L, 2L, 99.99, 5000L, 2500L);

        String json = new ObjectMapper()
                .writeValueAsString(ticket1);
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created

        // Step 2: Get all tickets to find the ID of the ticket we just created
        String ticketReturned = mockMvc.perform(get("/api/tickets"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first ticket from the paginated response
        int CurrentID =  JsonPath.read(ticketReturned, "$.content[0].id");

        // Step 3: Test PUT to update the ticket's sold count
        mockMvc.perform(put("/api/tickets/id/"+CurrentID+"/sold/20000"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful update
    }
    @Transactional
    @Test
    void doesPostNotCreateATicketWithMissingDataReturns400() throws Exception{
        // Test: POST request with missing required data should return 400 Bad Request
        // This tests validation pattern - similar to venue validation test
        // Testing the same validation pattern as venues

        // Send POST request with incomplete ticket data (missing price)
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event_id": 1, "total_quantity": 100, "sold": 50}
                                """)
                )
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request for validation failure
    }

}