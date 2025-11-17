package com.alexey.repository.Parsers;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BigDecimalParser {
    BigDecimal parseFromResultSet(ResultSet resultSet, int column) throws SQLException;

    BigDecimal parseFromResultSet(ResultSet resultSet, String columnName) throws SQLException;

    void writeToStatement(PreparedStatement statement, int parameterIndex, BigDecimal value) throws SQLException;
}
