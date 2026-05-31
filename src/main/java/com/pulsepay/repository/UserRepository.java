package com.pulsepay.repository;

import com.pulsepay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Inherits standard CRUD operations from JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom finders for unique identifiers
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    // Checkers for registration logic
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
