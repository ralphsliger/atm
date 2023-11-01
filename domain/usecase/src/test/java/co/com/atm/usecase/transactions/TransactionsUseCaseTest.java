package co.com.atm.usecase.transactions;

import co.com.atm.model.account.Account;
import co.com.atm.model.account.gateways.AccountRepository;
import co.com.atm.model.transaction.Transaction;
import co.com.atm.model.transaction.gateways.TransactionRepository;
import co.com.atm.model.transactiontype.TransactionType;
import co.com.atm.usecase.accountusecase.AccountUseCase;
import co.com.atm.usecase.checkbalance.CheckBalanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionsUseCaseTest {

    @Mock
    private AccountUseCase accountUseCase;

    @Mock
    private CheckBalanceUseCase checkBalanceUseCase;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionsUseCase transactionsUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionsUseCase = new TransactionsUseCase(accountUseCase, checkBalanceUseCase, accountRepository, transactionRepository);
    }

    @Test
    void makeWithdrawal_ValidWithdrawal_ReturnsWithdrawalTransaction() {
        // Arrange
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("500");
        Account account = new Account(accountId, "123456789", new BigDecimal("1000"));
        Transaction withdrawalTransaction = new Transaction();

        when(accountUseCase.validateAccountExistence(accountId)).thenReturn(account);
        when(transactionRepository.saveTransaction(any())).thenReturn(withdrawalTransaction);

        // Act
        Transaction result = transactionsUseCase.makeWithdrawal(accountId, amount);

        // Assert
        assertNotNull(result);
        verify(accountUseCase).validateAccountExistence(accountId);
        verify(accountRepository).saveAccount(account);
        verify(transactionRepository).saveTransaction(any());
    }

}
