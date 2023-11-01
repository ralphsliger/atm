package co.com.atm.jpa.account;

import co.com.atm.jpa.helper.AdapterOperations;
import co.com.atm.jpa.mapper.MapperObject;
import co.com.atm.model.account.Account;
import co.com.atm.model.account.gateways.AccountRepository;
import jakarta.transaction.Transactional;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountAdapterRepository extends AdapterOperations<Account, AccountDataEntity, Long, AccountDataRepository> implements AccountRepository   {

    private final MapperObject mapperObject;

    public AccountAdapterRepository(AccountDataRepository repository, ObjectMapper mapper, MapperObject mapperObject) {
        super(repository, mapper, d -> mapper.map(d, Account.class/* change for domain model */));
        this.mapperObject = mapperObject;
    }

    @Override
    public Optional<Account> findAccountById(Long accountId) {
        Optional<AccountDataEntity> account = repository.findById(accountId);

        if (account.isPresent()) {
            AccountDataEntity accountDataEntity = account.get();
            return Optional.of(mapperObject.mapToAccount(accountDataEntity));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Account saveAccount(Account account) {

        AccountDataEntity accountDataEntity = mapperObject.mapToAccountDataEntity(account);

        AccountDataEntity savedAccountDataEntity = repository.save(accountDataEntity);

        return mapperObject.mapToAccount(savedAccountDataEntity);
    }

    @Override
    public List<Account> findAllAccounts() {
        List<AccountDataEntity> accountDataEntities = repository.findAll();
        return mapperObject.mapToAccountList(accountDataEntities);

    }



}
