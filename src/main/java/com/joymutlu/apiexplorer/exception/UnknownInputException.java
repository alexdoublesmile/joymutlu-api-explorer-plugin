package com.joymutlu.apiexplorer.exception;

public class UnknownInputException extends Exception {
    private static final String DEFAULT_MESSAGE = "Caret should stay on exploring object or type";
    private final String message;

    public UnknownInputException(String message) {
        super(message);
        this.message = message;
    }
    public UnknownInputException() {
        this.message = DEFAULT_MESSAGE;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
