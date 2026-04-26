package org.treyenwilson.capstone.eventbooking.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.treyenwilson.capstone.eventbooking.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Basic JpaRepository provides all CRUD operations including findAll(Pageable)
    
    // Find user by username for authentication
    Optional<User> findByUsername(String username);
}

