package org.treyenwilson.capstone.eventbooking.dto;
import java.time.LocalDate;


public class VenueResponse {


    private Long id;
    private String venue_name;
    private LocalDate date;
    private String location;
    private Long total_capacity;
    private Long venue_id;
//    private String description;
//
//    private String venue;
//    private Long total_capacity;
//    private Long tickets_sold;

    //getters and setters below

    public VenueResponse(){}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }



    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }



    public Long getTotal_capacity() { return total_capacity; }
    public void setTotal_capacity(Long total_capacity) { this.total_capacity = total_capacity; }
    public Long getVenue_id() { return venue_id; }
    public void setVenue_id(Long venue_id) { this.venue_id = venue_id; }

    /*  public Long getTickets_sold() { return tickets_sold; }
    public void setTickets_sold(Long tickets_sold) { this.tickets_sold = tickets_sold; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

     */
}
