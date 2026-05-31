package com.pulsepay.service;

import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.TransactionStatus;
import com.pulsepay.entities.TransactionType;
import com.pulsepay.entities.User;
import com.pulsepay.repository.TransactionRepository;
import com.pulsepay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service class for handling financial transactions.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction transferMoney(Long senderId, Long receiverId, BigDecimal amount) {

        //1. validations
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount has to be more than 0");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender id has to be different than receiver id");
        }
        //2. fetch users
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        //3. business rules
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds.");
            }
        //4. mutate state
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        //5. create audit record
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);

        //6. save record
        return transactionRepository.save(transaction);
    }
}
