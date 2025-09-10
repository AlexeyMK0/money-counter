package com.alexey.model;

import java.math.BigDecimal;

public class TransactionRecord {

    private int id;
    private BigDecimal moneyAmount;
    private String operationType;

    public TransactionRecord(int id, BigDecimal moneyAmount, String operationType) {
        this.id = id;
        this.moneyAmount = moneyAmount;
        this.operationType = operationType;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getMoney() {
        return moneyAmount;
    }

    public String getType() {
        return operationType;
    }
}
