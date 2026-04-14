package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;


public class VenueRequest {
    @NotBlank(message = "You must provide a name for the venue")
    private String venue_name;

    @NotBlank(message = "You must provide a location")
    private String location;

    @NotNull(message = "Total spots must be more then 0")
    private Long total_capacity;

    @NotNull(message = "You must provide an id from the venue")
    private Long venue_id;

    public VenueRequest() {}

    public VenueRequest(String venue_name, LocalDate date, String location, Long total_capacity, Long venue_id) {
        this.venue_name = venue_name;
        this.location = location;
        this.total_capacity = total_capacity;
        this.venue_id = venue_id;
    }



    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }



    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Long getTotal_capacity() { return total_capacity; }
    public void setTotal_capacity(Long total_capacity) { this.total_capacity = total_capacity; }
    public Long getVenue_id() { return venue_id; }
    public void setVenue_id(Long venue_id) { this.venue_id = venue_id; }

}
