package com.alexey.repository;

import com.alexey.model.Account;
import com.alexey.model.AccountInfo;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findAccount(int id);

    List<Account> getAccounts();

    Account insertAccount(AccountInfo accountInfo);
}
