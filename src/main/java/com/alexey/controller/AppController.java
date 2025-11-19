package com.alexey.controller;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionInfo;
import com.alexey.model.TransactionRecord;

import java.util.List;

public interface AppController {
    List<Account> getAllAccounts();

    List<Category> getAllCategories();

    List<TransactionRecord> getTransactionsByAccount(Account account);

    TransactionRecord insertNewTransaction(TransactionInfo transactionInfo);
}
