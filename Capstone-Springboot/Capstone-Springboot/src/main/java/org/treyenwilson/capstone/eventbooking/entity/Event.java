package org.treyenwilson.capstone.eventbooking.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String event_name;
    private LocalDate date;
    private String status;
    private Long total_spots;
    private Long venue_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnoreProperties({"events"})
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<Ticket> tickets;

//    @NotBlank
//    private String name;
//    private String description;
//
//    private String venue;
//    private Long total_capacity;
//    private Long tickets_sold;


    //getters and setters below
    public Event(){}

    public Event(Long id, String event_name, LocalDate date, String status, Long total_spots, Long venue_id){
        this.id = id;
        this.date = date;
        this.status = status;
        this.total_spots = total_spots;
        this.venue_id = venue_id;
        this.event_name = event_name;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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

    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }

    public java.util.List<Ticket> getTickets() { return tickets; }
    public void setTickets(java.util.List<Ticket> tickets) { this.tickets = tickets; }

//    public Long getTickets_sold() { return tickets_sold; }
//    public void setTickets_sold(Long tickets_sold) { this.tickets_sold = tickets_sold; }
//

//    public String getVenue() { return venue; }
//    public void setVenue(String venue) { this.venue = venue; }
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }


}
