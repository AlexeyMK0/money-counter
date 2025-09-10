package com.alexey.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.alexey.model.Account;
import com.alexey.model.TransactionRecord;

public class AccountRepository {

    private static final List<String> NAMES = List.of("Revolut", "Deutsche Bank");

    public Account getAccount(int id) {
        return new Account(NAMES.get(id % NAMES.size()), id, 
            List.of(
                new TransactionRecord(1, new BigDecimal("123"), "grocceries"),
                new TransactionRecord(2, new BigDecimal("321"), "rent")
            ));
    }
}
