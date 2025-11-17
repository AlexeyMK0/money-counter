package com.alexey.repository.Parsers;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FromLongFixedPointBigDecimalParser implements BigDecimalParser {
    private final int precision;

    public FromLongFixedPointBigDecimalParser(int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision must be not negative");
        }
        this.precision = precision;
    }

    @Override
    public BigDecimal parseFromResultSet(ResultSet resultSet, int column) throws SQLException {
        long value = resultSet.getLong(column);
        return parseFromLong(value);
    }

    @Override
    public BigDecimal parseFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        return parseFromLong(value);
    }

    @Override
    public void writeToStatement(PreparedStatement statement, int parameterIndex, BigDecimal value) throws SQLException {
        statement.setLong(parameterIndex, convertToLong(value));
    }

    private long convertToLong(BigDecimal value) {
        return value.movePointRight(precision).longValue();
    }
    private BigDecimal parseFromLong(long value) {
        return BigDecimal.valueOf(value).movePointLeft(precision);
    }
}
