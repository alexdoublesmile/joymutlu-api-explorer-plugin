package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.util.StringUtils;

import static java.lang.Character.isLowerCase;

public class UserInputInfo {
    private final String input;
    private final InputType inputType;
    private final String filter;
    private final int startPosition;
    private final int endPosition;
    private final int caretOffset;

    public UserInputInfo(String input, String filter, int startPosition, int endPosition, int caretOffset) {
        this.input = input;
        this.filter = filter;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.caretOffset = caretOffset;

        inputType = StringUtils.isUnknown(input)
                ? InputType.UNKNOWN
                : isLowerCase(input.charAt(0)) ? InputType.OBJECT : InputType.TYPE;
    }

    public String value() {
        return input;
    }

    public InputType getInputType() {
        return inputType;
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
