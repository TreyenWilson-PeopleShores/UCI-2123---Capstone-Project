package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    //List<Venue> findByStatus(String status);

    Page<Venue> findByLocation(Pageable pageable, String location);

    Page<Venue> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);

}

