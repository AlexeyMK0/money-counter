package com.alexey.repository.impl.exceptions;

public class KeyNotReturnedFromDataBaseException extends DataAccessException {
    public KeyNotReturnedFromDataBaseException() {
        super("Key not returned");
    }
}
