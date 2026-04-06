package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //List<Event> findByStatus(String status);

    Page<Event> findByStatus(Pageable pageable, String status);

    //    @Query("SELECT events from events where"+
//    "(:status = 'CANCELLED')");

}

