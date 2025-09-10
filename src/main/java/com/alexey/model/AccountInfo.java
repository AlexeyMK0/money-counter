package com.alexey.model;

public class AccountInfo {
    
    private final int id;
    private final String name;

    public AccountInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
