package com.alexey.repository.impl;

import com.alexey.model.Account;
import com.alexey.model.AccountInfo;
import com.alexey.model.Category;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.AccountRepository;
import com.alexey.repository.CategoryRepository;
import com.alexey.repository.DataBase;
import com.alexey.repository.TransactionRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DataBaseRepositoryAdapter implements DataBase {
    private final AccountRepository accountRepository;

    private final CategoryRepository categoryRepository;

    private final TransactionRepository transactionRepository;

    public DataBaseRepositoryAdapter(AccountRepository accountRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
    }

    @Override
    public Optional<Account> findAccount(int id) {
        return accountRepository.findAccount(id);
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.getAccounts();
    }

    @Override
    public Account insertAccount(AccountInfo accountInfo) { return accountRepository.insertAccount(accountInfo); }

    @Override
    public Optional<Category> findCategory(int categoryId) {
        return categoryRepository.findCategory(categoryId);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.getCategories();
    }

    @Override
    public Optional<TransactionRecord> findTransactionById(int transactionId) {
        return transactionRepository.findTransactionById(transactionId);
    }

    @Override
    public List<TransactionRecord> getTransactionsByAccount(Account account) {
        return transactionRepository.getTransactionsByAccount(account);
    }

    @Override
    public List<TransactionRecord> getTransactionsByCategory(Category category) {
        return transactionRepository.getTransactionsByCategory(category);
    }
}
