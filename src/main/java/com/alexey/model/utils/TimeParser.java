package com.alexey.model.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TimeParser {
    public static Optional<Instant> tryParseDateToInstant(String line, DateTimeFormatter formatter, ZoneId timeZone) {
        return Optional.of(LocalDateTime
                .parse(line, formatter)
                .atZone(timeZone)
                .toInstant());
    }
}
