package com.alexey.repository.impl;

import com.alexey.model.Account;
import com.alexey.repository.AccountRepository;
import com.alexey.repository.impl.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AccountRepositoryFromConnection implements AccountRepository {

    private final Connection connection;

    private static String accountSelectByIdQuery(int id) {
        return String.format("SELECT id, account_name FROM account WHERE id = %d", id);
    }

    private static String allAccountsSelectQuery() {
        return "SELECT * FROM account";
    }

    public AccountRepositoryFromConnection(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public Optional<Account> findAccount(int id) {
        try {
            Optional<Account> returnValue = Optional.empty();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(accountSelectByIdQuery(id));
            if (resultSet.next()) {
                returnValue = Optional.of(parseAccount(resultSet));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    @Override
    public List<Account> getAccounts() {
        try {
            var returnValue = new ArrayList<Account>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(allAccountsSelectQuery());
            while (resultSet.next()) {
                returnValue.add(parseAccount(resultSet));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    private Account parseAccount(ResultSet resultSet) throws SQLException {
        return new Account(resultSet.getInt("id"), resultSet.getString("account_name"));
    }
}
