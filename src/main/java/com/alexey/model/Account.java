package com.alexey.model;

import java.util.List;

public class Account {
    private final int id;
    private final String name;
    private List<TransactionRecord> transactions;

    public Account(String name, int id, List<TransactionRecord> transactionRecords) {
        this.id = id;
        this.name = name;
        transactions = transactionRecords;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<TransactionRecord> getTransactionRecords() {
        return transactions;
    }
}
