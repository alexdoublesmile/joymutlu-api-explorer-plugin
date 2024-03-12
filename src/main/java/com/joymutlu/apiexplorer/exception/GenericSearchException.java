package com.joymutlu.apiexplorer.exception;

public class GenericSearchException extends Exception {
    private final String message;
    public GenericSearchException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
