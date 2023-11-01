package co.com.atm.model.transaction.gateways;

import co.com.atm.model.transaction.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction saveTransaction(Transaction transaction);

    List<Transaction> findAllByAccountId(Long accountId);

    List<Transaction> findAllTransactions();
}
