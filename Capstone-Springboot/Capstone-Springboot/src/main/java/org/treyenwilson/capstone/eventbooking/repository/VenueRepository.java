package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findByLocation(Pageable pageable, String location);

    @Query("SELECT v FROM Venue v WHERE v.location LIKE %:city%")
    Page<Venue> findByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT v FROM Venue v WHERE v.location LIKE %:state")
    Page<Venue> findByState(@Param("state") String state, Pageable pageable);
}

