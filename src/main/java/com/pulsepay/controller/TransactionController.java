package com.pulsepay.controller;

import com.pulsepay.dto.TransactionResponse;
import com.pulsepay.dto.TransferRequest;
import com.pulsepay.entities.Transaction;
import com.pulsepay.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer (
            @RequestBody TransferRequest request){

        //1. delegate the logic to the service
        Transaction completedTransaction = transactionService.transferMoney(
                request.senderId(),
                request.receiverId(),
                request.amount()
        );
        //2. convert the sensitive database entity into a safe Response DTO
        TransactionResponse responseDto = TransactionResponse.fromEntity(completedTransaction);

        //3. return a 200 OK HTTP status along with the JSON receipt
        return ResponseEntity.ok(responseDto);
    }
}
