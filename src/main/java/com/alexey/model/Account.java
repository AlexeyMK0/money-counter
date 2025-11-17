package com.alexey.model;

import java.util.List;

public class Account {
    private final int id;
    private final String name;

    public Account(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Account fromAccountInfoAndId(AccountInfo info, int id) {
        return new Account(id, info.name());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
