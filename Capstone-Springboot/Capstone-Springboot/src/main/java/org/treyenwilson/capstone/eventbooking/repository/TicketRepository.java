package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
@Query("SELECT t FROM Ticket t WHERE t.event_id = :event_id")
    Page<Ticket> findByEvent_id(Pageable pageable, @Param("event_id") Long event_id);

    @Query("SELECT t FROM Ticket t WHERE t.price > :minPrice")
    Page<Ticket> findByPriceGreaterThan(@Param("minPrice") Double minPrice, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.price < :maxPrice")
    Page<Ticket> findByPriceLessThan(@Param("maxPrice") Double maxPrice, Pageable pageable);
}

