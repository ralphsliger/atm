package co.com.atm.model.account.gateways;

import co.com.atm.model.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findAccountById(Long accountId);
    Account saveAccount(Account account);
    List<Account> findAllAccounts();
}
