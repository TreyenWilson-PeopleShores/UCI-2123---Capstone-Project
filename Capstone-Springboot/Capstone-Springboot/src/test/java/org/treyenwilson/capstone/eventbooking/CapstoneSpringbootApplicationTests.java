package org.treyenwilson.capstone.eventbooking;

import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.treyenwilson.capstone.eventbooking.controller.EventController;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CapstoneSpringbootApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void doesPostWorkGivesTrueIfItDoes() throws Exception {

        String event3 = """
                {"id":"1","event_name": "An event", "date":"2004-01-01", "status":"COMPLETED", "total_spots":"32137", "venue_id":"1"}
                """;



        Event event1 = new Event(1L,"An event", LocalDate.of(2004,01,01), "COMPLETED", 32137L, 1L);
        Event event2 = new Event(2L,"An event2", LocalDate.of(2007,01,01), "CANCELLED", 337L, 1L);
        mockMvc.perform(post("/api/events"));
        mockMvc.perform(get("/api/events/id/1"))
                .andExpect(status().isOk());



    }

}