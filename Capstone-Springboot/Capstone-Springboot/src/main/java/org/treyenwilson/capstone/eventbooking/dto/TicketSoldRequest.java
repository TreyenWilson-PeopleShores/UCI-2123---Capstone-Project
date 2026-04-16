package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TicketSoldRequest {

    @NotNull(message = "User ID is required")
    private Long user_id;

    @NotNull(message = "Ticket ID is required")
    private Long ticket_id;

    @NotNull(message = "Date sold is required")
    private LocalDate date_sold;

    //getters and setters below

    public TicketSoldRequest(){}
    
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public Long getTicket_id() { return ticket_id; }
    public void setTicket_id(Long ticket_id) { this.ticket_id = ticket_id; }

    public LocalDate getDate_sold() { return date_sold; }
    public void setDate_sold(LocalDate date_sold) { this.date_sold = date_sold; }
}