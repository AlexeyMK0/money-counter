package com.alexey.repository.impl;

import com.alexey.model.Category;
import com.alexey.model.CategoryInfo;
import com.alexey.repository.CategoryRepository;
import com.alexey.repository.impl.exceptions.DataAccessException;
import com.alexey.repository.impl.exceptions.KeyNotReturnedFromDataBaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CategoryRepositoryFromConnection implements CategoryRepository {
    private final static String insertCategoryQuery = "INSERT INTO transaction_category(category_name) VALUES(?)";

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

    @Override
    public Category insertCategory(CategoryInfo categoryInfo) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    insertCategoryQuery, Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, categoryInfo.name());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new KeyNotReturnedFromDataBaseException();
            }
            return Category.fromCategoryInfoAndId(categoryInfo, resultSet.getInt(1));
        } catch (SQLException e) {
            throw new DataAccessException("Error while inserting " + categoryInfo + ": " + e);
        }
    }

    private Category parseCategory(ResultSet resultSet) throws SQLException {
        return new Category(resultSet.getInt("id"), resultSet.getString("category_name"));
    }
}
