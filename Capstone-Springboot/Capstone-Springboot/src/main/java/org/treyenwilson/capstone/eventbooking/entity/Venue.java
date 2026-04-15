package org.treyenwilson.capstone.eventbooking.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "venues")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String venue_name;
    private String location;
    private Long total_capacity;


    //getters and setters below
    public Venue(){}

    public Venue(Long id, String venue_name, String location, Long total_capacity){
        this.id = id;
        this.location = location;
        this.total_capacity = total_capacity;
        this.venue_name = venue_name;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Long getTotal_capacity() { return total_capacity; }
    public void setTotal_capacity(Long total_capacity) { this.total_capacity = total_capacity; }



}
