package com.pulsepay.repository;

import com.pulsepay.entities.Transaction;
import com.pulsepay.entities.TransactionStatus;
import com.pulsepay.entities.TransactionType;
import com.pulsepay.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //1. Spin up in -memory database  and configure JPA
class TransactionRepositoryTest {

    //2. a special helper give us in @DataJpaTest to insert raw data easily
    @Autowired
    private TestEntityManager entityManager; //instead of UserRepository to rely only on tested repository

    //3. the actual component we want to test
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should a find trasaction by its unique external reference")
    void findByTransactionReference_Success() {
        //1. ARRANGE

        // because of foreign keys, we have to set real users in the DB first
        User sender = User.builder()
                .username("sender1")
                .email("sender1@test.com")
                .password("secret")
                .balance(BigDecimal.TEN)
                .build();

        User receiver = User.builder()
                .username("receiver1")
                .email("receiver1@test.com")
                .password("secret")
                .balance(BigDecimal.ZERO)
                .build();


        // use the entity manager to send them directly to db
        entityManager.persist(sender);
        entityManager.persist(receiver);

        //Now construct the transaction that we want to save
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("5.00"));
        transaction.setCurrency("EUR");
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setType(TransactionType.TRANSFER);

        //save the transaction and force Hibernate to insert SQL immediately (flush)
        Transaction saved = entityManager.persistAndFlush(transaction); // flush is faster because it is not waiting for INSERT

        //take the UUID when the object was generated
        String referenceToSearchFor = saved.getTransactionReference();

        //2. ACT
        //check if repository method works
        Optional<Transaction> result = transactionRepository.findByTransactionReference(referenceToSearchFor);

        //3. ASSERT
        assertThat(result).isPresent(); //check if found anything

        //check fields if it is the same transaction
        assertThat(result.get().getTransactionReference()).isEqualTo(referenceToSearchFor);
        assertThat(result.get().getAmount()).isEqualByComparingTo("5.00");
        assertThat(result.get().getSender().getUsername()).isEqualTo("sender1");

    }


}
