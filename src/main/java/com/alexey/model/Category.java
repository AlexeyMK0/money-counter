package com.alexey.model;

public class Category {

    public static Category Unknown() {
        return new Category(0, null);
    }

    private final int id;

    private final String name;

    public Category(int id, String name) {
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
