package com.alexey.model.utils;

import com.alexey.model.Account;
import com.alexey.model.Category;

import java.util.List;

public class ModelPrinter {
    public static void printAccounts(List<Account> accounts, boolean addNumeration) {
        int i = 1;
        for (Account acc : accounts) {
            System.out.println((addNumeration ? (i + ". ") : "") + acc.getName());
            i++;
        }
    }

    public static void printCategories(List<Category> categories, boolean addNumeration) {
        int i = 1;
        for (Category category : categories) {
            System.out.println((addNumeration ? (i + ". ") : "") + category.getName());
            i++;
        }
    }
}
