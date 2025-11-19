package com.alexey.controller;

import java.util.List;
import java.util.Optional;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionInfo;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.DataBase;

public class DataBaseAppController implements AppController {
    private final DataBase repository;

    public DataBaseAppController(DataBase repository) {
        this.repository = repository;
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.getAccounts();
    }

    @Override
    public List<Category> getAllCategories() {
        return repository.getCategories();
    }

    @Override
    public List<TransactionRecord> getTransactionsByAccount(Account account) {
        return repository.getTransactionsByAccount(account);
    }

    @Override
    public TransactionRecord insertNewTransaction(TransactionInfo transactionInfo) {
        return repository.insertTransaction(transactionInfo);
    }
}
