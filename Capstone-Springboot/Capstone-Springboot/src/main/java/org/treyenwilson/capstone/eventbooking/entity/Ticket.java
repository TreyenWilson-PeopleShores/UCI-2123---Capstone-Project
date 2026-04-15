package org.treyenwilson.capstone.eventbooking.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long event_id;
    private Double price;
    private Long total_quantity;
    private Long sold;


    //getters and setters below
    public Ticket(){}

    public Ticket(Long id, Long event_id, Double price, Long total_quantity, Long sold){
        this.id = id;
        this.event_id = event_id;
        this.price = price;
        this.total_quantity = total_quantity;
        this.sold = sold;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEvent_id() { return event_id; }
    public void setEvent_id(Long event_id) { this.event_id = event_id; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Long getTotal_quantity() { return total_quantity; }
    public void setTotal_quantity(Long total_quantity) { this.total_quantity = total_quantity; }
    public Long getSold() { return sold; }
    public void setSold(Long sold) { this.sold = sold; }



}
