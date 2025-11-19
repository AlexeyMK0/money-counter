package com.alexey.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.alexey.controller.AppController;
import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionInfo;
import com.alexey.model.TransactionRecord;
import com.alexey.model.utils.ModelPrinter;
import com.alexey.model.utils.TimeParser;

public class InputStreamInterface {

    private final DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("uuuu:MM:dd_HH:mm");
    private final AppController controller;
    private Account curAccount;
    private BufferedReader reader;

    private enum CommandType {
        EXIT,
        SHOW_OPERATIONS,
        SHOW_ACCOUNTS,
        SHOW_CATEGORIES,
        SELECT_ACCOUNT,
        HELP,
        BAD_COMMAND,
        INSERT_TRANSACTION,
    }

    public InputStreamInterface(AppController controller) {
        this.controller = controller;
        curAccount = null;
    }

    public void run(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            mainCycle();
        } catch (IOException exception) {
            System.out.println("IOException happened: " + exception);
        } catch (Exception ex) {
            System.out.println("Exception happened: " + ex);
        }
        finally {
            closeResources();
        }
    }

    private void mainCycle() throws IOException {
        boolean needToExit = false;
        while (!needToExit) {
            CommandType type = readCommand();
            System.out.println("Command type: " + type);
            needToExit |= executeCommand(type);
        }

        System.out.println("Goodbye!");
    }

    private void closeResources() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("reader.close has thrown and exception: " + e);
            }
        }
    }

    private CommandType readCommand() throws IOException {
        String command = reader.readLine();
        if (command == null) {
            return CommandType.EXIT;
        }
        if (command.equalsIgnoreCase("SHOW_OPERATIONS")) {
            return CommandType.SHOW_OPERATIONS;
        } else if (command.equalsIgnoreCase("EXIT")) {
            return CommandType.EXIT;
        } else if (command.equalsIgnoreCase("HELP")) {
            return CommandType.HELP;
        } else if (command.equalsIgnoreCase("SHOW_ACCOUNTS")) {
            return CommandType.SHOW_ACCOUNTS;
        } else if (command.toUpperCase().startsWith("SELECT_ACCOUNT")) {
            return CommandType.SELECT_ACCOUNT;
        } else if (command.toUpperCase().startsWith("INSERT_TRANSACTION")) {
            return CommandType.INSERT_TRANSACTION;
        }
        return CommandType.BAD_COMMAND;
    }
    private boolean executeCommand(CommandType command) throws IOException {
        boolean needToExit = false;
        switch (command) {
            case SHOW_OPERATIONS -> showCurrentAccountOperations();
            case BAD_COMMAND -> System.out.println("Bad command, try again");
            case HELP -> showHelp();
            case EXIT -> needToExit = true;
            case SHOW_ACCOUNTS -> showAccounts();
            case SHOW_CATEGORIES -> showCategories();
            case SELECT_ACCOUNT -> selectAccount();
            case INSERT_TRANSACTION -> insertTransaction();
             default -> System.out.println("What's just happened? " + command.toString());
        }
        return needToExit;
    }

    private void showHelp() {
        for (var commandType : CommandType.values()) {
            System.out.println(commandType);
        }
    }

    private void showAccounts() {
        ModelPrinter.printAccounts(controller.getAllAccounts(), false);
    }

    private void showCategories() {
        ModelPrinter.printCategories(controller.getAllCategories(), false);
    }

    private void showCurrentAccountOperations() {
        if (curAccount == null) {
            System.out.println("No account selected. Please select account to show operations");
            return;
        }

        var operations = controller.getTransactionsByAccount(curAccount);
        System.out.println(String.format("%s\t|%s\t|%s", "id", "value", "type"));
        for (final var operation : operations) {
            showOperation(operation);
        }
    }

    private void showOperation(TransactionRecord transaction) {
        System.out.println(String.format(
            "%d\t|%s\t|%s", transaction.getId(), transaction.getMoney(), transaction.getCategory().getName()));
    }

    private void selectAccount() {
        var accounts = controller.getAllAccounts();
        System.out.println("Enter the index of account or any other number to cancel");
        ModelPrinter.printAccounts(accounts, true);
        curAccount = pickOneFromConsoleInputIfInRangeOrDefault(
            accounts, 0, accounts.size(), 1, curAccount);
    }

    private void insertTransaction() throws IOException {
        if (curAccount == null) {
            System.out.println("No account selected. Please select account to insert transaction");
            return;
        }
        // TODO: create TransactionInfoBuilder
        TransactionInfo info = new TransactionInfo(
                curAccount,
                inputMoney(),
                pickOneOfTheCategories(controller.getAllCategories()),
                inputDate());
        controller.insertNewTransaction(info);
    }

    // [from, to)
    // input must be from 'from + offset' to 'to + offset'
    private <T extends Object> T pickOneFromConsoleInputIfInRangeOrDefault(List<T> accounts, int from, int to, int offset, T defaultReturn) {
        String input;
        int index;
        try {
            input = reader.readLine();
            index = Integer.parseInt(input);
        } catch (IOException | NumberFormatException ex) {
            return defaultReturn;
        }

        index -= offset;
        if (index < from || to <= index) {
            return defaultReturn;
        }

        return accounts.get(index);
    }

    private Instant inputDate() throws IOException {
        System.out.println("Type date in format yyyy:mm:dd_hh:mm");
        System.out.println("Or else current date will be selected");

        Instant retValue = Instant.now();

        String line;
        line = reader.readLine();

        return TimeParser.tryParseDateToInstant(line, inputDateFormatter, ZoneOffset.UTC).orElse(retValue);
    }

    private BigDecimal inputMoney() throws IOException {
        System.out.println("Input money in format 1234.56");
        String line = reader.readLine();

        // TODO: input validation
        BigDecimal retValue = new BigDecimal(line);

        return retValue;
    }

    private Category pickOneOfTheCategories(List<Category> categories) throws IOException {
        System.out.println("Select one of the indexes of the categories or first will be selected");
        ModelPrinter.printCategories(categories, true);
        return pickOneFromConsoleInputIfInRangeOrDefault(
                categories, 0, categories.size(), 1, categories.get(0));
    }
}

