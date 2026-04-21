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
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VenueTests {
    
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
    void doesGetFailAtGrabbingAVenueIdThatDoesNotExistShouldReturn404() throws Exception {
        // Test: GET request for a non-existent venue ID should return 404 Not Found
        // This tests the error handling when trying to retrieve a venue that doesn't exist

        mockMvc.perform(get("/api/venues/id/99999999999999999"))
                .andExpect(status().isNotFound()); // Expect HTTP 404 status

    }



    @Transactional
    @Test
    void isGetAbleToGrabAVenueByID() throws Exception {
        // Test: GET request for an existing venue by ID should return 200 OK
        // This tests the successful retrieval of a venue by its ID

        // Step 1: Create a test venue in the database
        MockitoAnnotations.openMocks(this);
        Venue venue1 = new Venue(1L,"Concert Hall", "New York, NY", 5000L);
        MockitoAnnotations.openMocks(this);
        Venue venue2 = new Venue(2L,"Stadium", "Los Angeles, CA", 80000L);

        String json = new ObjectMapper()
                .writeValueAsString(venue1);
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created for successful POST

        // Step 2: Get all venues to find the ID of the venue we just created
        String venueReturned = mockMvc.perform(get("/api/venues"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first venue from the paginated response
        int CurrentID =  JsonPath.read(venueReturned, "$.content[0].id");

        // Step 3: Test GET by ID with the extracted venue ID
        mockMvc.perform(get("/api/venues/id/"+CurrentID))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful GET

    }


    // ============================================
    // POST Tests
    // ============================================
    
    @Transactional
    @Test
    void ifPostWorksItReturns201() throws Exception{
        // Test: POST request to create a new venue should return 201 Created
        // This tests the successful creation of a venue

        // Create test venue data
        MockitoAnnotations.openMocks(this);
        Venue venue1 = new Venue(1L,"Concert Hall", "New York, NY", 5000L);
        MockitoAnnotations.openMocks(this);
        Venue venue2 = new Venue(2L,"Stadium", "Los Angeles, CA", 80000L);

        // Convert venue object to JSON
        String json = new ObjectMapper()
                .writeValueAsString(venue1);
        
        // Send POST request to create venue
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created
    }


    // ============================================
    // PUT Tests
    // ============================================
    
    // Test: PUT request to update venue location should return 200 OK
    @Transactional
    @Test
    void isPutAbleToModifyAVenueShouldReturn200() throws Exception {
        // This tests the successful update of a venue's location

        // Step 1: Create a test venue in the database
        Venue venue1 = new Venue(1L,"Concert Hall", "New York, NY", 5000L);
        Venue venue2 = new Venue(2L,"Stadium", "Los Angeles, CA", 80000L);

        String json = new ObjectMapper()
                .writeValueAsString(venue1);
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created

        // Step 2: Get all venues to find the ID of the venue we just created
        String venueReturned = mockMvc.perform(get("/api/venues"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first venue from the paginated response
        int CurrentID =  JsonPath.read(venueReturned, "$.content[0].id");

        // Step 3: Test PUT to update the venue's location
        mockMvc.perform(put("/api/venues/id/"+CurrentID+"/Chicago, IL"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful update
    }
    @Transactional
    @Test
    void doesPostNotCreateAVenueWithMissingDataReturns400() throws Exception{
        // Test: POST request with missing required data should return 400 Bad Request
        // This tests validation - venue requires venue_name, location, AND total_capacity
        // Here we're missing total_capacity, so it should fail validation

        // Send POST request with incomplete venue data (missing total_capacity)
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"venue_name": "Test Venue", "location":"New York"}
                                """)
                )
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request for validation failure
    }

}