package com.joymutlu.apiexplorer.exception;

public class NotImplementedException extends Exception {
    private final String message;
    public NotImplementedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}