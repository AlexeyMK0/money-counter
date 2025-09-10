package com.alexey;

import com.alexey.controller.AppController;
import com.alexey.repository.AccountRepository;
import com.alexey.view.UserInterface;

public class Main {
    public static void main(String[] args) {
        AppController controller = new AppController(new AccountRepository());
        UserInterface view = new UserInterface(controller);
        view.run();
    }
}