package com.joymutlu.apiexplorer.exception;

public class NoApiException extends Exception {
    private final String message;
    public NoApiException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
