package org.treyenwilson.capstone.eventbooking.dto;
import java.time.LocalDate;

public class TicketSoldResponse {

    private Long id;
    private Long user_id;
    private Long ticket_id;
    private LocalDate date_sold;

    //getters and setters below

    public TicketSoldResponse(){}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }
    
    public Long getTicket_id() { return ticket_id; }
    public void setTicket_id(Long ticket_id) { this.ticket_id = ticket_id; }
    
    public LocalDate getDate_sold() { return date_sold; }
    public void setDate_sold(LocalDate date_sold) { this.date_sold = date_sold; }
}