package com.joymutlu.apiexplorer.exception;

public class NoImportException extends Exception {
    private final String message;
    public NoImportException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
