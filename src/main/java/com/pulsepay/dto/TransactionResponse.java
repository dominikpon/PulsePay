package com.pulsepay.dto;

import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionReference,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        LocalDateTime timestamp
) {
    public static TransactionResponse fromEntity(Transaction ts){
        return new TransactionResponse(
                ts.getTransactionReference(),
                ts.getAmount(),
                ts.getCurrency(),
                ts.getStatus(),
                ts.getTimestamp()
        );
    }
}
