package org.treyenwilson.capstone.eventbooking.dto;


public class VenueResponse {


    private Long id;
    private String venue_name;
    private String location;
    private Long total_capacity;

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

}

