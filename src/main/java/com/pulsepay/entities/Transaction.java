package com.pulsepay.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String transactionReference = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")

    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency; //e.g. USD

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private LocalDateTime timestamp = LocalDateTime.now();
}
