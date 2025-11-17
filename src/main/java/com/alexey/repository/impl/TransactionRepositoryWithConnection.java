package com.alexey.repository.impl;

import com.alexey.model.Account;
import com.alexey.model.Category;
import com.alexey.model.TransactionInfo;
import com.alexey.model.TransactionRecord;
import com.alexey.repository.Parsers.BigDecimalParser;
import com.alexey.repository.Parsers.FromLongFixedPointBigDecimalParser;
import com.alexey.repository.TransactionRepository;
import com.alexey.repository.impl.exceptions.DataAccessException;
import com.alexey.repository.impl.exceptions.KeyNotReturnedFromDataBaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TransactionRepositoryWithConnection implements TransactionRepository {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd_HH:mm:ss");

    public static final String insertTransactionQuery = "INSERT INTO `transaction`(created_at, sum, account_id, category_id) VALUES(?,?,?,?)";

    private static String transactionSelectWithAccountCategoryInfoByIdQuery(int transactionId) {
        return String.format(
                """
                SELECT t.id AS id, t.created_at AS created_at, t.sum AS sum,
                    t.account_id AS account_id, a.account_name AS account_name,
                    t.category_id AS category_id, tc.category_name AS category_name
                FROM `transaction` AS t
                JOIN account AS a
                    ON t.account_id = a.id
                LEFT JOIN transaction_category AS tc
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
                LEFT JOIN transaction_category AS tc
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

    private final BigDecimalParser bigDecimalParser;

    public TransactionRepositoryWithConnection(Connection connection) {
        this(connection, new FromLongFixedPointBigDecimalParser(2));
    }

    public TransactionRepositoryWithConnection(Connection connection, BigDecimalParser bigDecimalParser) {
        this.connection = Objects.requireNonNull(connection);
        this.bigDecimalParser = Objects.requireNonNull(bigDecimalParser);
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

    @Override
    public TransactionRecord insertTransaction(TransactionInfo transactionInfo) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                insertTransactionQuery, Statement.RETURN_GENERATED_KEYS
            );
            fillTransactionInsertStatement(preparedStatement, transactionInfo);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new KeyNotReturnedFromDataBaseException();
            }
            return TransactionRecord.fromInfoAndId(transactionInfo, resultSet.getInt(1));
        } catch (SQLException e) {
            throw new DataAccessException("Error while inserting " + transactionInfo + ": " + e);
        }
    }

    private TransactionRecord parseTransactionByIdFromAllJoinedQuery(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Instant time = parseTransactionDate(resultSet, "created_at");
        BigDecimal sum = parseTransactionSum(resultSet, "sum");
        Account account = parseAccount(resultSet, "account_name", "account_id");
        Category category = parseCategory(resultSet, "category_name", "category_id");

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
        Category category = parseCategory(resultSet, "category_name", "category_id");

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
        Account account = parseAccount(resultSet, "account_name", "account_id");

        return new TransactionRecord(
                id,
                account,
                sum,
                category,
                time
        );
    }

    private Category parseCategory(ResultSet resultSet, String nameColumnName, String idColumnName) throws SQLException {
        Integer id = resultSet.getObject(idColumnName, Integer.class);
        String categoryName = resultSet.getString(nameColumnName);
        return new Category(id, categoryName);
    }

    private Account parseAccount(ResultSet resultSet, String nameColumnName, String idColumnName) throws  SQLException {
        return new Account(resultSet.getInt(idColumnName), resultSet.getString(nameColumnName));
    }

    private BigDecimal parseTransactionSum(ResultSet resultSet, String columnName) throws  SQLException {
        return bigDecimalParser.parseFromResultSet(resultSet, columnName);
    }

    private Instant parseTransactionDate(ResultSet resultSet, String columnName) throws SQLException {
        String tm = resultSet.getString(columnName);
        return LocalDateTime
                .parse(tm, DATE_TIME_FORMATTER)
                .atZone(ZoneOffset.UTC)
                .toInstant();
    }

    private void fillTransactionInsertStatement(PreparedStatement statement, TransactionInfo transactionInfo) throws SQLException {
        statement.setString(1, DATE_TIME_FORMATTER.format(transactionInfo.dateTime().atZone(ZoneId.systemDefault())));
        bigDecimalParser.writeToStatement(statement, 2, transactionInfo.moneyAmount());
        statement.setInt(3, transactionInfo.account().getId());
        statement.setInt(4, transactionInfo.category().getId());
    }
}
