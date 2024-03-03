package com.joymutlu.apiexplorer.model;

public class UserInput {
    private final String input;
    private final String filter;
    private final int startPosition;
    private final int endPosition;
    private final int caretOffset;

    public UserInput(String input, String filter, int startPosition, int endPosition, int caretOffset) {
        this.input = input;
        this.filter = filter;
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

    public String getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return input;
    }
}
