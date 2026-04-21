package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;


public class TicketRequest {

    @NotNull(message = "Event ID is required")
    private Long event_id;

    @NotNull(message = "Price must be provided")
    private Double price;

    @NotNull(message = "Total quantity must be provided")
    private Long total_quantity;

    @NotNull(message = "Sold quantity must be provided")
    private Long sold;

    //getters and setters below

    public TicketRequest(){}
    public Long getEvent_id() { return event_id; }
    public void setEvent_id(Long event_id) { this.event_id = event_id; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }



    public Long getTotal_quantity() { return total_quantity; }
    public void setTotal_quantity(Long total_quantity) { this.total_quantity = total_quantity; }



    public Long getSold() { return sold; }
    public void setSold(Long sold) { this.sold = sold; }

}

