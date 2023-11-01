package co.com.atm.usecase.accountusecase;

import co.com.atm.model.account.Account;
import co.com.atm.model.account.gateways.AccountRepository;
import co.com.atm.usecase.exceptions.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountUseCase accountUsecase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountUsecase = new AccountUseCase(accountRepository);
    }

    @Test
    void validateAccountExistence_ExistingAccountId_ReturnsAccount() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account(accountId, "John Doe", BigDecimal.ONE);

        when(accountRepository.findAccountById(accountId)).thenReturn(Optional.of(account));

        Account result = accountUsecase.validateAccountExistence(accountId);

        assertEquals(account, result);
        verify(accountRepository).findAccountById(accountId);
    }

    @Test
    void validateAccountExistence_NonExistingAccountId_ThrowsAccountNotFoundException() {
        Long accountId = 2L;

        when(accountRepository.findAccountById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountUsecase.validateAccountExistence(accountId));
        verify(accountRepository).findAccountById(accountId);
    }

    @Test
    void saveAccount_ValidAccount_ReturnsSavedAccount() {
        Account account = new Account(1L, "John Doe", BigDecimal.ONE);

        when(accountRepository.saveAccount(account)).thenReturn(account);

        Account result = accountUsecase.saveAccount(account);

        assertEquals(account, result);
        verify(accountRepository).saveAccount(account);
    }

    @Test
    void accounts_NoAccounts_ReturnsEmptyList() {
        when(accountRepository.findAllAccounts()).thenReturn(new ArrayList<>());

        List<Account> result = accountUsecase.accounts();

        assertTrue(result.isEmpty());
        verify(accountRepository).findAllAccounts();
    }

    @Test
    void accounts_AccountsExist_ReturnsAccountList() {
        List<Account> accounts = List.of(
                new Account(1L, "John Doe", BigDecimal.ONE),
                new Account(2L, "Jane Smith", BigDecimal.ONE)
        );

        when(accountRepository.findAllAccounts()).thenReturn(accounts);

        // Act
        List<Account> result = accountUsecase.accounts();

        assertEquals(accounts, result);
        verify(accountRepository).findAllAccounts();
    }
}
