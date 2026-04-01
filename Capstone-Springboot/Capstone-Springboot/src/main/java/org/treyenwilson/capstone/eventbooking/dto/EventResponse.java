package org.treyenwilson.capstone.eventbooking.dto;
import java.util.List;

import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private LocalDateTime date;
    private String status;
//    private String name;
//    private String description;
//
//    private String venue;
//    private Long total_capacity;
//    private Long tickets_sold;

    //getters and setters below

    public EventResponse(){}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    /*


    public Long getTotal_capacity() { return total_capacity; }
    public void setTotal_capacity(Long total_capacity) { this.total_capacity = total_capacity; }
    public Long getTickets_sold() { return tickets_sold; }
    public void setTickets_sold(Long tickets_sold) { this.tickets_sold = tickets_sold; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

     */
}
