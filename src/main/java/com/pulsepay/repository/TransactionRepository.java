package com.pulsepay.repository;

import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity.
 * Inherits standard CRUD operations from JpaRepository.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find a transaction by its unique external reference
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    // Find transactions involving a specific user
    List<Transaction> findBySender(User sender);
    List<Transaction> findByReceiver(User receiver);
    
    // Combined history for a user, ordered by most recent
    List<Transaction> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
}
