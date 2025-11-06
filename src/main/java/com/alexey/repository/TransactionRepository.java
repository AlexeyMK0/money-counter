package com.alexey.repository;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionRecord;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<TransactionRecord> findTransactionById(int transactionId);

    List<TransactionRecord> getTransactionsByAccount(Account account);

    List<TransactionRecord> getTransactionsByCategory(Category category);
}
