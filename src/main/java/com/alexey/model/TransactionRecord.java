package com.alexey.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRecord {

    private final int id;

    private final Account account;

    private final BigDecimal moneyAmount;
    private final Category category;
    private LocalDateTime dateTime;


    public TransactionRecord(int id, Account account, BigDecimal moneyAmount, Category category) {
        this.id = id;
        this.account = account;
        this.moneyAmount = moneyAmount;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }
}
