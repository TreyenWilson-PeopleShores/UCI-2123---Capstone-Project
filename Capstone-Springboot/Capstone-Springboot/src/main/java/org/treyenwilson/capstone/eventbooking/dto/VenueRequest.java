package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;


public class VenueRequest {
    @NotBlank(message = "You must provide a name for the venue")
    @jakarta.validation.constraints.Size(max = 200, message = "Venue name must not exceed 200 characters")
    private String venue_name;

    @NotBlank(message = "You must provide a location")
    @jakarta.validation.constraints.Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotNull(message = "Total spots must be more then 0")
    @jakarta.validation.constraints.Positive(message = "Total capacity must be a positive number")
    private Long total_capacity;

    public VenueRequest() {}

    public VenueRequest(String venue_name, String location, Long total_capacity) {
        this.venue_name = venue_name;
        this.location = location;
        this.total_capacity = total_capacity;
    }



    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }



    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Long getTotal_capacity() { return total_capacity; }
    public void setTotal_capacity(Long total_capacity) { this.total_capacity = total_capacity; }

}
