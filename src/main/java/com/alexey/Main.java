package com.alexey;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.alexey.controller.AppController;
import com.alexey.repository.impl.DataBaseDummy;
import com.alexey.view.InputStreamInterface;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputData = """
                SELECT_ACCOUNT
                1
                SHOW_OPERATIONS
                SELECT_ACCOUNT
                2
                SHOW_OPERATIONS
                """;
        InputStream inputStream = new ByteArrayInputStream(
            inputData.getBytes(StandardCharsets.UTF_8)
        );
//        InputStream inputStream = System.in;
        var controller = new AppController(new DataBaseDummy());
        var view = new InputStreamInterface(controller);
        view.run(inputStream);
    }
}