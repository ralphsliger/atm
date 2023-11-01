package co.com.atm.jpa.mapper;

import co.com.atm.jpa.account.AccountDataEntity;
import co.com.atm.jpa.transaction.TransactionDataEntity;
import co.com.atm.model.account.Account;
import co.com.atm.model.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class MapperObject {
    public AccountDataEntity mapToAccountDataEntity(Account account) {
        return AccountDataEntity.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    public Account mapToAccount(AccountDataEntity accountDataEntity) {
        return Account.builder()
                .id(accountDataEntity.getId())
                .accountNumber(accountDataEntity.getAccountNumber())
                .balance(accountDataEntity.getBalance())
                .build();
    }

    public List<Account> mapToAccountList(List<AccountDataEntity> accountDataEntity) {
        return accountDataEntity.stream().map(this::mapToAccount).collect(Collectors.toList());
    }

    public List<TransactionDataEntity> mapToTransactionDataEntities(List<Transaction> transactions, Long accountId) {
        return transactions.stream()
                .map(this::mapToTransactionDataEntity)
                .collect(Collectors.toList());
    }

    public List<Transaction> mapToTransactions(List<TransactionDataEntity> transactionDataEntities) {
        return transactionDataEntities.stream()
                .map(this::mapToTransaction)
                .collect(Collectors.toList());
    }

    public TransactionDataEntity mapToTransactionDataEntity(Transaction transaction) {
        return TransactionDataEntity.builder()
                .id(transaction.getId())
                .type(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .finalBalance(transaction.getFinalBalance())
                .description(transaction.getDescription())
                .accountId(transaction.getAccountId())
                .build();
    }

    public Transaction mapToTransaction(TransactionDataEntity transactionDataEntity) {
        return Transaction.builder()
                .id(transactionDataEntity.getId())
                .transactionType(transactionDataEntity.getType())
                .amount(transactionDataEntity.getAmount())
                .finalBalance(transactionDataEntity.getFinalBalance())
                .description(transactionDataEntity.getDescription())
                .accountId(transactionDataEntity.getAccountId())
                .build();
    }
}


