package com.alexey.repository.impl;

import com.alexey.model.Category;
import com.alexey.repository.CategoryRepository;
import com.alexey.repository.impl.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CategoryRepositoryFromConnection implements CategoryRepository {
    private static String categorySelectByIdQuery(int categoryId) {
        return String.format("SELECT id, category_name FROM transaction_category WHERE id = %d", categoryId);
    }

    private static String allCategoriesSelectQuery() {
        return "SELECT * FROM transaction_category";
    }

    private final Connection connection;

    public CategoryRepositoryFromConnection(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public Optional<Category> findCategory(int categoryId) {
        try {
            Optional<Category> returnValue = Optional.empty();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(categorySelectByIdQuery(categoryId));
            if (resultSet.next()) {
                returnValue = Optional.of(parseCategory(resultSet));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    @Override
    public List<Category> getCategories() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(allCategoriesSelectQuery());
            var returnValue = new ArrayList<Category>();
            while (resultSet.next()) {
                returnValue.add(parseCategory(resultSet));
            }
            return returnValue;
        } catch (SQLException e) {
            throw new DataAccessException("DataBase exception", e);
        }
    }

    private Category parseCategory(ResultSet resultSet) throws SQLException {
        return new Category(resultSet.getInt("id"), resultSet.getString("category_name"));
    }
}
