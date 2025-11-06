package com.alexey.repository.impl;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.DataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DataBaseWithConnection /*implements DataBase*/ {
    Connection connection;

    public DataBaseWithConnection(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    public Optional<Account> findAccount(int id) throws SQLException {
        Optional<Account> returnValue = Optional.empty();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT id, account_name FROM account WHERE id = %d", id));
        if (resultSet.next()) {
            returnValue = Optional.of(parseAccount(resultSet));
        }
        return returnValue;
    }

    public List<Account> getAccounts() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM account");
        var returnValue = new ArrayList<Account>();
        while (resultSet.next()) {
            returnValue.add(parseAccount(resultSet));
        }
        return returnValue;
    }

    public Optional<Category> findCategory(int categoryId) throws SQLException {
        Optional<Category> returnValue = Optional.empty();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT id, category_name FROM transaction_category WHERE id = %d", categoryId));
        if (resultSet.next()) {
            returnValue = Optional.of(parseCategory(resultSet));
        }
        return returnValue;
    }

    public List<Category> getCategories() throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM transaction_category");
        var returnValue = new ArrayList<Category>();
        while (resultSet.next()) {
            returnValue.add(parseCategory(resultSet));
        }
        return returnValue;
    }

//    @Override
//    public Optional<TransactionRecord> findTransactionById(int transactionId) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<TransactionRecord> getTransactionsByAccount(Account account) {
//        return List.of();
//    }
//
//    @Override
//    public List<TransactionRecord> getTransactionsByCategory(Category category) {
//        return List.of();
//    }

    private Account parseAccount(ResultSet resultSet) throws SQLException {
        return new Account(resultSet.getInt("id"), resultSet.getString("account_name"));
    }

    private Category parseCategory(ResultSet resultSet) throws SQLException {
        return new Category(resultSet.getInt("id"), resultSet.getString("category_name"));
    }
}
