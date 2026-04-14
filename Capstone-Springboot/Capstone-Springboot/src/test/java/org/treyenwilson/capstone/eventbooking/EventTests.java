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
import org.treyenwilson.capstone.eventbooking.entity.Event;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventTests {
    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;


    @Transactional
    @Test
    void doesGetFailAtGrabbingAnEventIdThatDoesNotExistShouldReturn404() throws Exception {
        // Test not passing

        mockMvc.perform(get("/api/events/id/99999999999999999"))
                .andExpect(status().isNotFound()); // Checks for a code of 201

    }



    @Transactional
    @Test
    void isGetAbleToGrabAnEventByID() throws Exception {



        // Post some mock data to the mock database
        MockitoAnnotations.openMocks(this);
        Event event1 = new Event(1L,"An event", LocalDate.of(2004,01,01), "COMPLETED", 32137L, 1L);
        MockitoAnnotations.openMocks(this);
        Event event2 = new Event(2L,"An event2", LocalDate.of(2007,01,01), "CANCELLED", 337L, 1L);

        String json = new ObjectMapper()
                .writeValueAsString(event1);
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Checks for a code of 201
                // Test for POSTing
        // POSTing is needed to add to the h2 database for the test GET to pass


        String eventReturned = mockMvc.perform(get("/api/events"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Long CurrentID = objectMapper.readTree(eventReturned).get("id").asLong();
        int CurrentID =  JsonPath.read(eventReturned, "$.content[0].id");



        mockMvc.perform(get("/api/events/id/"+CurrentID))
                .andExpect(status().isOk()); // This shows if GET is PASSing.

    }


    @Transactional
    @Test
    void ifPostWorksItReturns201() throws Exception{
// Post some mock data to the mock database
        MockitoAnnotations.openMocks(this);
        Event event1 = new Event(1L,"An event", LocalDate.of(2004,01,01), "COMPLETED", 32137L, 1L);
        MockitoAnnotations.openMocks(this);
        Event event2 = new Event(2L,"An event2", LocalDate.of(2007,01,01), "CANCELLED", 337L, 1L);

        String json = new ObjectMapper()
                .writeValueAsString(event1);
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Checks for a code of 201
        // Test for POSTing
    }


    // Tests PUT by changing STATUS
    @Transactional
    @Test
    void isPutAbleToModifyAnEventShouldReturn200() throws Exception {



        // Post some mock data to the mock database
        Event event1 = new Event(1L,"An event", LocalDate.of(2004,01,01), "COMPLETED", 32137L, 1L);
        Event event2 = new Event(2L,"An event2", LocalDate.of(2007,01,01), "CANCELLED", 337L, 1L);

        String json = new ObjectMapper()
                .writeValueAsString(event1);
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated()); // Checks for a code of 201
        // Test for POSTing
        // POSTing is needed to add to the h2 database for the test PUT to pass

       String eventReturned = mockMvc.perform(get("/api/events"))
                       .andReturn()
                            .getResponse()
                                .getContentAsString();

        //Long CurrentID = objectMapper.readTree(eventReturned).get("id").asLong();
      int CurrentID =  JsonPath.read(eventReturned, "$.content[0].id");

        mockMvc.perform(put("/api/events/id/"+CurrentID+"/CANCELLED"))
                .andExpect(status().isOk()); // This shows if PUT is PASSing.
    }
    @Transactional
    @Test
    void doesPostNotCreateAnEventWithMissingDataReturns400() throws Exception{
// Post some mock data to the mock database

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event_name": "Test", "status":"COMPLETED"}
                                """)
                )
                .andExpect(status().isBadRequest()); // Checks for a code of 201
        // Test for POSTing
    }

}