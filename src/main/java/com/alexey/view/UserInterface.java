package com.alexey.view;

import com.alexey.controller.AppController;
import com.alexey.model.Account;
import com.alexey.model.TransactionRecord;

public class UserInterface {
    
    private AppController controller;

    public UserInterface(AppController controller) {
        this.controller = controller;
    }

    public void run() {
        Account account = controller.getAccount(0);
        var operations = account.getTransactionRecords();
        System.out.println(String.format("%s\t|%s\t|%s", "id", "value", "type"));
        for (final var operation : operations) {
            showOperation(operation);
        }
    }

    private void showOperation(TransactionRecord transaction) {
        System.out.println(String.format("%d\t|%s\t|%s", transaction.getId(), transaction.getMoney(), transaction.getType()));
    }
}
