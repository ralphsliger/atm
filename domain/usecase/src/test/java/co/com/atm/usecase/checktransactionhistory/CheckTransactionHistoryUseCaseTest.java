package co.com.atm.usecase.checktransactionhistory;

import co.com.atm.model.account.Account;
import co.com.atm.model.transaction.Transaction;
import co.com.atm.usecase.accountusecase.AccountUseCase;
import co.com.atm.model.transaction.gateways.TransactionRepository;
import co.com.atm.usecase.exceptions.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckTransactionHistoryUseCaseTest {

    @Mock
    private AccountUseCase accountUseCase;

    @Mock
    private TransactionRepository transactionRepository;

    private CheckTransactionHistoryUseCase checkTransactionHistoryUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkTransactionHistoryUseCase = new CheckTransactionHistoryUseCase(accountUseCase, transactionRepository);
    }

    @Test
    void getTransactionHistoryByAccount_ExistingAccountId_ReturnsTransactionList() {
        Long accountId = 1L;
        Account account = new Account(accountId, "123456789", BigDecimal.ZERO);
        List<Transaction> transactions = new ArrayList<>();

        when(accountUseCase.validateAccountExistence(accountId)).thenReturn(account);
        when(transactionRepository.findAllByAccountId(accountId)).thenReturn(transactions);

        List<Transaction> result = checkTransactionHistoryUseCase.getTransactionHistoryByAccount(accountId);

        assertEquals(transactions, result);
        verify(accountUseCase).validateAccountExistence(accountId);
        verify(transactionRepository).findAllByAccountId(accountId);
    }

    @Test
    void getTransactionHistoryByAccount_NonExistingAccountId_ThrowsAccountNotFoundException() {
        Long accountId = 2L;

        when(accountUseCase.validateAccountExistence(accountId)).thenThrow(new AccountNotFoundException("Account not found"));

        assertThrows(AccountNotFoundException.class, () -> checkTransactionHistoryUseCase.getTransactionHistoryByAccount(accountId));
        verify(accountUseCase).validateAccountExistence(accountId);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void getAll_ReturnsAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        when(transactionRepository.findAllTransactions()).thenReturn(transactions);

        List<Transaction> result = checkTransactionHistoryUseCase.getAll();

        assertEquals(transactions, result);
        verify(transactionRepository).findAllTransactions();
        verifyNoInteractions(accountUseCase);
    }
}
