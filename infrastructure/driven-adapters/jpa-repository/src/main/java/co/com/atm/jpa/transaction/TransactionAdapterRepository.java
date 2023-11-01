package co.com.atm.jpa.transaction;

import co.com.atm.jpa.account.AccountDataEntity;
import co.com.atm.jpa.helper.AdapterOperations;
import co.com.atm.jpa.mapper.MapperObject;
import co.com.atm.model.account.Account;
import co.com.atm.model.transaction.Transaction;
import co.com.atm.model.transaction.gateways.TransactionRepository;
import jakarta.transaction.Transactional;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactionAdapterRepository extends AdapterOperations<Transaction, TransactionDataEntity, Long, TransactionDataRepository> implements TransactionRepository {

    private final MapperObject mapperObjects;

    public TransactionAdapterRepository(TransactionDataRepository repository, ObjectMapper mapper, MapperObject mapperObjects) {
        super(repository, mapper, d -> mapper.map(d, Transaction.class));
        this.mapperObjects = mapperObjects;
    }

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        TransactionDataEntity transactionDataEntity = mapperObjects.mapToTransactionDataEntity(transaction);
        TransactionDataEntity savedTransactionDataEntity = repository.save(transactionDataEntity);
        return mapperObjects.mapToTransaction(savedTransactionDataEntity);
    }

    @Override
    public List<Transaction> findAllByAccountId(Long accountId) {
       List<TransactionDataEntity> dataEntities = repository.findAllByAccountId(accountId);
        return mapperObjects.mapToTransactions(dataEntities);
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return  mapperObjects.mapToTransactions(repository.findAll()).stream().sorted(Comparator.comparing(Transaction::getId)).collect(Collectors.toList());
    }
}

