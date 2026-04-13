package org.treyenwilson.capstone.eventbooking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.treyenwilson.capstone.eventbooking.service.EventService;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;




@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CapstoneSpringbootApplicationTests {
    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void doesPostWorkGivesTrueIfItDoes() throws Exception {

        String event3 = """
                {"id":"1","event_name": "An event", "date":"2004-01-01", "status":"COMPLETED", "total_spots":"32137", "venue_id":"1"}
                """;


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

        mockMvc.perform(get("/api/events/id/1"))
                .andExpect(status().isOk()); // This shows if GET is PASSing.



    }

}