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
import org.treyenwilson.capstone.eventbooking.entity.User;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserTests {

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
    void doesGetFailAtGrabbingAUserIdThatDoesNotExistShouldReturn404() throws Exception {
        // Test: GET request for a non-existent user ID should return 404 Not Found
        // This tests the error handling when trying to retrieve a user that doesn't exist

        mockMvc.perform(get("/api/users/id/99999999999999999"))
                .andExpect(status().isNotFound()); // Expect HTTP 404 status

    }



    @Transactional
    @Test
    void isGetAbleToGrabAUserByID() throws Exception {
        // Test: GET request for an existing user by ID should return 200 OK
        // This tests the successful retrieval of a user by its ID

        // Step 1: Create a test user in the database
        MockitoAnnotations.openMocks(this);
        User user1 = new User(1L, "john_doe", "password123", "USER");
        MockitoAnnotations.openMocks(this);
        User user2 = new User(2L, "jane_smith", "securepass", "ADMIN");

        String json = new ObjectMapper()
                .writeValueAsString(user1);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created for successful POST

        // Step 2: Get all users to find the ID of the user we just created
        String userReturned = mockMvc.perform(get("/api/users"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the first user from the paginated response
        int CurrentID =  JsonPath.read(userReturned, "$.content[0].id");

        // Step 3: Test GET by ID with the extracted user ID
        mockMvc.perform(get("/api/users/id/"+CurrentID))
                .andExpect(status().isOk()); // Expect HTTP 200 OK for successful GET

    }


    // ============================================
    // POST Tests
    // ============================================

    @Transactional
    @Test
    void ifPostWorksItReturns201() throws Exception{
        // Test: POST request to create a new user should return 201 Created
        // This tests the successful creation of a user

        // Create test user data
        MockitoAnnotations.openMocks(this);
        User user1 = new User(1L, "john_doe", "password123", "USER");
        MockitoAnnotations.openMocks(this);
        User user2 = new User(2L, "jane_smith", "securepass", "ADMIN");

        // Convert user object to JSON
        String json = new ObjectMapper()
                .writeValueAsString(user1);

        // Send POST request to create user
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Expect HTTP 201 Created
    }




    @Transactional
    @Test
    void doesPostNotCreateAUserWithMissingDataReturns400() throws Exception{
        // Test: POST request with missing required data should return 400 Bad Request
        // This tests validation pattern - similar to other validation tests
        // Testing the same validation pattern as other entities

        // Send POST request with incomplete user data (missing password)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "testuser", "role": "USER"}
                                """)
                )
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request for validation failure
    }

}