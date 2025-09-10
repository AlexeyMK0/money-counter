package com.alexey.controller;

import java.math.BigDecimal;
import java.util.List;

import com.alexey.model.Account;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.AccountRepository;

public class AppController {

    private AccountRepository repository;

    public AppController(AccountRepository repository) {
        this.repository = repository;
    }

    public Account getAccount(int id) {
        return repository.getAccount(id);
    }

    public List<TransactionRecord> getTransactions(int accountId) {
        return getAccount(accountId).getTransactionRecords();
    }
}
