package com.pulsepay.service;


import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.TransactionStatus;
import com.pulsepay.entities.User;
import com.pulsepay.repository.TransactionRepository;
import com.pulsepay.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // enabling Mockito
public class TransactionServiceTest {

    //create fake repositories not to hit real DB
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;

    //create the real service and Mockito will automatically inject mocks
    @InjectMocks
    TransactionService transactionService;

    //define some standard users we will use across multiple tests
    private User sender;
    private User receiver;

    @BeforeEach // this will run before every test to ensure a clean state
    void setUp() {
        sender = User.builder()
                .id(1L)
                .username("sender")
                .balance(new BigDecimal("100.00"))
                .build();
        receiver = User.builder()
                .id(2L)
                .username("receiver")
                .balance (new BigDecimal("50.00"))
                .build();
    }

    @Test
    @DisplayName("Succesfully transfer money between two valid users")
    void transferMoney_Success() {

        //1. ARRANGE - set up the specific condition for this test
        BigDecimal  transferAmount = new BigDecimal("30.00");

        // tell fake UserRepository how to behave:
        // When the service asks for User ID 1 , give it our sender object
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        // When the service asks for User ID 2 , give it our receiver object
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        // We tell the fake TransactionRepository how to behave
        // When the service tells you to save any transaction, just return exactly what it gave
        Mockito.when(transactionRepository.save(any()))
                .thenAnswer(invocation ->  invocation.getArgument(0));

        //2. ACT - execute the method we are just testing
        Transaction result = transactionService.transferMoney(1L,2L,transferAmount);

        //3. ASSERT - verify the outcome was the one that was expected

        // did the service return a transaction object?
        assertThat(result).isNotNull();

        //did the balance update correctly?
        assertThat(sender.getBalance()).isEqualByComparingTo("70.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("80.00");

        //check transaction details
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

        //verify the repository was called to save the state
        verify(transactionRepository).save(any(Transaction.class));
    }
}
