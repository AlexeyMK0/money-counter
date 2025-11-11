package com.alexey.repository.impl;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.TransactionRepository;
import com.alexey.repository.impl.exceptions.DataAccessException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TransactionRepositoryWithConnection implements TransactionRepository {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private static String transactionSelectWithAccountCategoryInfoByIdQuery(int transactionId) {
        return String.format(
                """
                SELECT t.id AS id, t.created_at AS created_at, t.sum AS sum,
                    t.account_id AS account_id, a.account_name AS account_name,
                    t.category_id AS category_id, tc.category_name AS category_name
                FROM `transaction` AS t
                JOIN account AS a
                    ON t.account_id = a.id
                JOIN transaction_category AS tc
                    ON t.category_id = tc.id
                WHERE t.id = %d
                """, transactionId);
    }

    private static String transactionSelectWithCategoryInfoByAccountQuery(Account account) {
        return String.format(
                """
                SELECT t.id AS id, t.created_at AS created_at, t.sum AS sum,
                    t.account_id AS account_id,
                    t.category_id AS category_id, tc.category_name AS category_name
                FROM `transaction` AS t
                JOIN transaction_category AS tc
                    ON t.category_id = tc.id
                WHERE t.account_id = %d
                """, account.getId()
        );
    }
    private static String transactionSelectWithAccountInfoByCategoryQuery(Category category) {
        return String.format(
                """
                SELECT t.id AS id, t.created_at AS created_at, t.sum AS sum,
                    t.account_id AS account_id, a.account_name AS account_name,
                    t.category_id AS category_id
                FROM `transaction` AS t
                JOIN account AS a
                    ON t.account_id = a.id
                WHERE t.category_id = %d
                """, category.getId()
        );
    }

    private final Connection connection;

    public TransactionRepositoryWithConnection(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public Optional<TransactionRecord> findTransactionById(int transactionId) {
        try {
            Optional<TransactionRecord> returnValue = Optional.empty();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(transactionSelectWithAccountCategoryInfoByIdQuery(transactionId));
            if (resultSet.next()) {
                returnValue = Optional.of(parseTransactionByIdFromAllJoinedQuery(resultSet));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    @Override
    public List<TransactionRecord> getTransactionsByAccount(Account account) {
        try {
            List<TransactionRecord> returnValue = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(transactionSelectWithCategoryInfoByAccountQuery(account));
            while (resultSet.next()) {
                returnValue.add(parseTransactionByAccountFromJoinedWithCategoryQuery(resultSet, account));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    @Override
    public List<TransactionRecord> getTransactionsByCategory(Category category) {
        try {
            List<TransactionRecord> returnValue = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(transactionSelectWithAccountInfoByCategoryQuery(category));
            while (resultSet.next()) {
                returnValue.add(parseTransactionByCategoryFromJoinedWithAccountQuery(resultSet, category));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    private Category createCategory(int id, String name) {
        if (name == null) {
            return Category.Unknown();
        }
        return new Category(id, name);
    }

    private TransactionRecord parseTransactionByIdFromAllJoinedQuery(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Instant time = parseTransactionDate(resultSet, "created_at");
        BigDecimal sum = parseTransactionSum(resultSet, "sum");
        Account account = new Account(
                resultSet.getInt("account_id"), resultSet.getString("account_name"));
        Category category = createCategory(
                resultSet.getInt("category_id"), resultSet.getString("category_name"));

        return new TransactionRecord(
                id,
                account,
                sum,
                category,
                time
        );
    }

    private TransactionRecord parseTransactionByAccountFromJoinedWithCategoryQuery(ResultSet resultSet, Account account) throws SQLException {
        int id = resultSet.getInt("id");
        Instant time = parseTransactionDate(resultSet, "created_at");
        BigDecimal sum = parseTransactionSum(resultSet, "sum");
        Category category = createCategory(
                resultSet.getInt("category_id"), resultSet.getString("category_name"));

        return new TransactionRecord(
                id,
                account,
                sum,
                category,
                time
        );
    }

    private TransactionRecord parseTransactionByCategoryFromJoinedWithAccountQuery(ResultSet resultSet, Category category) throws SQLException {
        int id = resultSet.getInt("id");
        Instant time = parseTransactionDate(resultSet, "created_at");
        BigDecimal sum = parseTransactionSum(resultSet, "sum");
        Account account = new Account(
                resultSet.getInt("account_id"), resultSet.getString("account_name"));

        return new TransactionRecord(
                id,
                account,
                sum,
                category,
                time
        );
    }

    private BigDecimal parseTransactionSum(ResultSet resultSet, String columnName) throws  SQLException {
        return BigDecimal.valueOf(resultSet.getLong(columnName));
    }
    private Instant parseTransactionDate(ResultSet resultSet, String columnName) throws SQLException {
        String tm = resultSet.getString(columnName);
        return LocalDateTime
                .parse(tm, DATE_TIME_FORMATTER)
                .atZone(ZoneOffset.UTC)
                .toInstant();
    }
}
