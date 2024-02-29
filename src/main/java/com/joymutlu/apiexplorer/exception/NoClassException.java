package com.joymutlu.apiexplorer.exception;

public class NoClassException extends Exception {
    private final String message;
    public NoClassException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
