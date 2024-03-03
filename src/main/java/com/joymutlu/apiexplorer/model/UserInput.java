package com.joymutlu.apiexplorer.model;

public class UserInput {
    private final String input;
    private final int startPosition;
    private final int endPosition;
    private final int caretOffset;

    public UserInput(String input, int startPosition, int endPosition, int caretOffset) {
        this.input = input;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.caretOffset = caretOffset;
    }

    public String value() {
        return input;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getCaretOffset() {
        return caretOffset;
    }

    @Override
    public String toString() {
        return input;
    }
}
