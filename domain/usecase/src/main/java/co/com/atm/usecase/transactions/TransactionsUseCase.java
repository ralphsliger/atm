package co.com.atm.usecase.transactions;

import co.com.atm.model.account.Account;
import co.com.atm.model.account.gateways.AccountRepository;
import co.com.atm.model.transaction.Transaction;
import co.com.atm.model.transaction.gateways.TransactionRepository;
import co.com.atm.model.transactiontype.TransactionType;
import co.com.atm.usecase.accountusecase.AccountUseCase;
import co.com.atm.usecase.checkbalance.CheckBalanceUseCase;
import co.com.atm.usecase.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class TransactionsUseCase {

    private final AccountUseCase accountUsecaseUseCase;
    private final CheckBalanceUseCase checkBalanceUseCase;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public Transaction makeWithdrawal(Long accountId, BigDecimal amount){
        Account account = accountUsecaseUseCase.validateAccountExistence(accountId);
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        Transaction withdrawal = this.makeTransaction(accountId, TransactionType.WITHDRAWAL, amount, newBalance, "Withdrawal");
        accountRepository.saveAccount(account);
        return withdrawal;
    }

    public Transaction makeTransaction(Long account, TransactionType transactionType, BigDecimal amount, BigDecimal finalBalance, String description){
        Transaction transaction = Transaction
                .builder()
                .accountId(account)
                .transactionType(transactionType)
                .amount(amount)
                .finalBalance(finalBalance)
                .description(description)
                .build();
        return transactionRepository.saveTransaction(transaction);
    }

    public void makeTransfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount){
        Account sourceAccount = accountUsecaseUseCase.validateAccountExistence(sourceAccountId);
        BigDecimal oldBalance = sourceAccount.getBalance();

        Account destinationAccount = accountUsecaseUseCase.validateAccountExistence(destinationAccountId);
        checkBalanceUseCase.validateBalance(sourceAccountId, amount);

        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
        sourceAccount.setBalance(newSourceBalance);

        BigDecimal newDestinationBalance = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(newDestinationBalance);

        String transferDescription = "Transferencia de " + sourceAccount.getAccountNumber() + " a " + destinationAccount.getAccountNumber();

        this.makeWithdrawal(sourceAccountId, newSourceBalance);
        this.makeDeposit(destinationAccountId, newDestinationBalance);
        this.makeTransaction(sourceAccountId, TransactionType.TRANSFER, oldBalance, newSourceBalance ,transferDescription);

        accountUsecaseUseCase.saveAccount(sourceAccount);
        accountUsecaseUseCase.saveAccount(destinationAccount);


    }
    public Transaction makeDeposit(Long accountId, BigDecimal amount) throws AccountNotFoundException {

        Account account = accountUsecaseUseCase.validateAccountExistence(accountId);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        Transaction deposit = this.makeTransaction(accountId, TransactionType.DEPOSIT, amount, newBalance, "Deposit");

        accountUsecaseUseCase.saveAccount(account);

        return deposit;

    }
}
