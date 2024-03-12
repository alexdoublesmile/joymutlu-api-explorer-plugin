package com.joymutlu.apiexplorer.exception;

public class NoMethodException extends Exception {
    private final String message;
    public NoMethodException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
