package org.treyenwilson.capstone.eventbooking.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tickets_sold")
public class TicketSold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "ticket_id")
    private Long ticketId;
    
    @Column(name = "date_sold")
    private LocalDate dateSold;

    //getters and setters below
    public TicketSold(){}

    public TicketSold(Long id, Long userId, Long ticketId, LocalDate dateSold){
        this.id = id;
        this.userId = userId;
        this.ticketId = ticketId;
        this.dateSold = dateSold;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    
    public LocalDate getDateSold() { return dateSold; }
    public void setDateSold(LocalDate dateSold) { this.dateSold = dateSold; }
}