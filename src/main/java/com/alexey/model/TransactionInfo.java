package com.alexey.model;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionInfo(Account account, BigDecimal moneyAmount, Category category, Instant dateTime) {
}
