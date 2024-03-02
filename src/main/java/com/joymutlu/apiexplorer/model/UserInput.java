package com.joymutlu.apiexplorer.model;

public class UserInput {
    private final String input;
    private final int startPosition;
    private final int endPosition;

    public UserInput(String input, int startPosition, int endPosition) {
        this.input = input;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String value() {
        return input;
    }

    public int startPosition() {
        return startPosition;
    }

    public int endPosition() {
        return endPosition;
    }

    @Override
    public String toString() {
        return input;
    }
}
