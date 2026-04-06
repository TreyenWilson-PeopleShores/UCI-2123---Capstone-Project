package org.treyenwilson.capstone.eventbooking.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.processing.Generated;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String status;
    private Long total_spots;
    private Long venue_id;
//    @NotBlank
//    private String name;
//    private String description;
//
//    private String venue;
//    private Long total_capacity;
//    private Long tickets_sold;


    //getters and setters below
    public Event(){}

    public Event(Long id, LocalDate date, String status, Long total_spots, Long venue_id){
        this.id = id;
        this.date = date;
        this.status = status;
        this.total_spots = total_spots;
        this.venue_id = venue_id;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTotal_spots() { return total_spots; }
    public void setTotal_spots(Long total_spots) { this.total_spots = total_spots; }


    public Long getVenue_id() { return venue_id; }
    public void setVenue_id(Long venue_id) { this.venue_id = venue_id; }
//    public Long getTickets_sold() { return tickets_sold; }
//    public void setTickets_sold(Long tickets_sold) { this.tickets_sold = tickets_sold; }
//

//    public String getVenue() { return venue; }
//    public void setVenue(String venue) { this.venue = venue; }
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }


}
