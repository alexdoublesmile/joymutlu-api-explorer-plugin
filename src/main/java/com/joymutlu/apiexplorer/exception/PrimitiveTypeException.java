package com.joymutlu.apiexplorer.exception;

public class PrimitiveTypeException extends Exception {
    private final String message;
    public PrimitiveTypeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
