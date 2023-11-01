package co.com.atm.usecase.accountusecase;

import co.com.atm.model.account.Account;
import co.com.atm.model.account.gateways.AccountRepository;
import co.com.atm.usecase.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;

    public Account validateAccountExistence(Long accountId) {
        return accountRepository.findAccountById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    public Account saveAccount(Account account) {
        return accountRepository.saveAccount(account);
    }

    public List<Account> accounts() {
        return accountRepository.findAllAccounts();
    }



}
