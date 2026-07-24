package com.pulsepay.service;

import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.TransactionStatus;
import com.pulsepay.entities.TransactionType;
import com.pulsepay.entities.User;
import com.pulsepay.repository.TransactionRepository;
import com.pulsepay.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class RewardService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public RewardService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional  public void processWorkoutReward(String username, double distanceKm){
        //1. fetch the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username not found" + username));
        //2. calculate the reward
        BigDecimal rewardAmount = BigDecimal.valueOf(distanceKm);

        //3. update the user balance
        user.setBalance(user.getBalance().add(rewardAmount));
        userRepository.save(user);

        //4. create a receipt of the payout
        Transaction receipt = new Transaction();
        receipt.setReceiver(user);
        receipt.setAmount(rewardAmount);
        receipt.setCurrency("PLP"); //PulsePay token
        receipt.setStatus(TransactionStatus.COMPLETED);
        receipt.setType(TransactionType.DEPOSIT);
        receipt.setTransactionReference(UUID.randomUUID().toString());

        transactionRepository.save(receipt);
    }

}
