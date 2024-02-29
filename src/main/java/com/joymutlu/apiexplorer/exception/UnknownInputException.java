package com.joymutlu.apiexplorer.exception;

public class UnknownInputException extends Exception {
    private final String message;
    public UnknownInputException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
