package com.alexey.controller;

import java.util.List;
import java.util.Optional;

import com.alexey.model.Account;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.DataBase;

public class AppController {

    private final DataBase repository;

    public AppController(DataBase repository) {
        this.repository = repository;
    }

    public Optional<Account> getAccount(int id) {
        return repository.findAccount(id);
    }

    public List<Account> getAccounts() {
        return repository.getAccounts();
    }

    public List<TransactionRecord> getTransactions(Account account) {
        return repository.getTransactionsByAccount(account);
    }
}
