package com.alexey.repository;

import com.alexey.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findAccount(int id);

    List<Account> getAccounts();
}
