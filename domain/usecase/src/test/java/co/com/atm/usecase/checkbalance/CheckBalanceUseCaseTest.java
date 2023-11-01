package co.com.atm.usecase.checkbalance;

import co.com.atm.model.account.Account;
import co.com.atm.usecase.accountusecase.AccountUseCase;
import co.com.atm.usecase.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckBalanceUseCaseTest {

    @Mock
    private AccountUseCase accountUseCase;

    private CheckBalanceUseCase checkBalanceUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkBalanceUseCase = new CheckBalanceUseCase(accountUseCase);
    }

    @Test
    void validateBalance_WithSufficientBalance_DoesNotThrowException() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account(accountId, "123456789", new BigDecimal("1500"));

        when(accountUseCase.validateAccountExistence(accountId)).thenReturn(account);

        assertDoesNotThrow(() -> checkBalanceUseCase.validateBalance(accountId, amount));
        verify(accountUseCase).validateAccountExistence(accountId);
    }

    @Test
    void validateBalance_WithInsufficientBalance_ThrowsInsufficientBalanceException() {
        // Arrange
        Long accountId = 2L;
        BigDecimal amount = new BigDecimal("500");
        Account account = new Account(accountId, "987654321", new BigDecimal("200"));

        when(accountUseCase.validateAccountExistence(accountId)).thenReturn(account);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class,
                () -> checkBalanceUseCase.validateBalance(accountId, amount));
        assertEquals("Insufficient balance in the account. Current balance: 200", exception.getMessage());
        verify(accountUseCase).validateAccountExistence(accountId);
    }
}
