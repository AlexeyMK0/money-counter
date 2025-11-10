package com.alexey.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class TransactionRecord {

    private final int id;

    private final Account account;

    private final BigDecimal moneyAmount;
    private final Category category;
    private Instant dateTime;

    public TransactionRecord(int id, Account account, BigDecimal moneyAmount, Category category, Instant dateTime) {
        this.id = id;
        this.account = account;
        this.moneyAmount = moneyAmount;
        this.category = category;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getMoney() {
        return moneyAmount;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public Category getCategory() {
        return category;
    }
}
