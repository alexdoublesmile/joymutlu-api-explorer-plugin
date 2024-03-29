package com.joymutlu.apiexplorer.exception;

public class NoInitializingLineException extends Exception {
    private static final String DEFAULT_MESSAGE = "Not found object initializing for the reference";

    private final String message;
    public NoInitializingLineException(String message) {
        this.message = message;
    }
    public NoInitializingLineException() {
        this.message = DEFAULT_MESSAGE;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
