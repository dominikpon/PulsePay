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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // enabling Mockito
class TransactionServiceTest {

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
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        // When the service asks for User ID 2 , give it our receiver object
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        // We tell the fake TransactionRepository how to behave
        // When the service tells you to save any transaction, just return exactly what it gave
        when(transactionRepository.save(any()))
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
    @Test
    @DisplayName("Should throw exception when sender has insufficient funds")
    void transferMoney_InsufficientFunds() {

        //1. ARRANGE
        BigDecimal transferAmount = new BigDecimal("120");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        //2. ACT & ASSERT
        //wrap the method inside the lambda () -> (....)
        //and it allows AssertJ to catch the exception instead of crashing the test
        assertThatThrownBy(() -> // clean and modern way to test exceptions with lambda
                transactionService.transferMoney(1L,2L,transferAmount))
                .isInstanceOf(IllegalStateException.class) // to check if it threw the right exception
                .hasMessage("Insufficient funds"); //did it have this exact message?

        //Critical assertion
        //to guarantee that if the transfer fails ,no money is moved and saved
        // Mockito's never() to ensure save() was never called on the repository
        verify(transactionRepository, never()).save(any());
        //crucial pattern in financial system -
        //not only has to check that exception was thrown,also ensure that it never accidentally calls save() method in repository
    }

    @Test
    @DisplayName("Should throw an exception when amount is ZERO or negative")
    void transferMoney_InvalidAmount() {
        //ACT & ASSERT
        //Test Zero
        assertThatThrownBy(() -> transactionService.transferMoney(1L,2L,BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount has to be more than 0");
        //Test negative
        assertThatThrownBy(() -> transactionService.transferMoney(1L,2L,new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class).
                hasMessage("Amount has to be more than 0");
        //Assert no interaction with database happened
        verifyNoInteractions(userRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should throw an exception when sender and receiver are the same user")
    void transferMoney_SameUser() {
        //ACT & ASSERT
        //senderId and receiverId will be the same - 1L

        assertThatThrownBy(() -> transactionService.transferMoney(1L,1L, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender id has to be different than receiver id");

        //Assert no database interaction happened
        verifyNoInteractions(userRepository, transactionRepository);

        //verifyNoInteraction() guarantees service will not even look at one of repositories - it proves that code fails fast
    }
}
