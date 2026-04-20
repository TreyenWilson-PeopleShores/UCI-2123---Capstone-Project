package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;

@Repository
public interface TicketSoldRepository extends JpaRepository<TicketSold, Long> {
    // Basic JpaRepository provides all CRUD operations including findAll(Pageable)
    
    @Query("SELECT t FROM TicketSold t WHERE MONTH(t.dateSold) = :month")
    Page<TicketSold> findByMonth(@Param("month") int month, Pageable pageable);
    
    @Query("SELECT t FROM TicketSold t WHERE YEAR(t.dateSold) = :year")
    Page<TicketSold> findByYear(@Param("year") int year, Pageable pageable);
    
    @Query("SELECT t FROM TicketSold t WHERE MONTH(t.dateSold) = :month AND YEAR(t.dateSold) = :year")
    Page<TicketSold> findByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);
    
    @Query("SELECT t FROM TicketSold t WHERE t.userId = :userId")
    Page<TicketSold> findByUserId(@Param("userId") Long userId, Pageable pageable);
}