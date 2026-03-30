package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}

