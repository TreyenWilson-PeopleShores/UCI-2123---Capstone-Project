package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;


public class EventRequest {
    @NotBlank(message = "You must provide a name for the event")
    private String event_name;

    @NotNull(message = "You must provide a date")
    private LocalDate date;

    @NotBlank(message = "You must provide a status")
    private String status;

    @NotNull(message = "Total spots must be more then 0")
    private Long total_spots;

    @NotNull(message = "You must provide an id from the venue")
    private Long venue_id;

    public EventRequest() {}

    public EventRequest(String event_name, LocalDate date, String status, Long total_spots, Long venue_id) {
        this.event_name = event_name;
        this.status = status;
        this.date = date;
        this.total_spots = total_spots;
        this.venue_id = venue_id;
    }



    public String getEvent_name() { return event_name; }
    public void setEvent_name(String event_name) { this.event_name = event_name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getTotal_spots() { return total_spots; }
    public void setTotal_spots(Long total_spots) { this.total_spots = total_spots; }
    public Long getVenue_id() { return venue_id; }
    public void setVenue_id(Long venue_id) { this.venue_id = venue_id; }

}
