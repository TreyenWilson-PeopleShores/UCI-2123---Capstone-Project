package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;


public class EventRequest {
    @NotNull(message = "You must provide a date")
    private LocalDate date;

    @NotBlank(message = "You provide a status")
    private String status;

    @NotNull(message = "Total spots must be more then 0")
    private Long total_spots;

    @NotNull(message = "You must provide an id from the venue")
    private Long venue_id;


    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getTotal_spots() { return total_spots; }
    public void setTotal_spots(Long total_spots) { this.total_spots = total_spots; }
    public Long getVenue_id() { return venue_id; }
    public void setVenue_id(Long venue_id) { this.venue_id = venue_id; }

}
