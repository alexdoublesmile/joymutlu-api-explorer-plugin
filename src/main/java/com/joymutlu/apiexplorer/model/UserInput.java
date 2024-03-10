package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.util.StringUtils;

import static java.lang.Character.isLowerCase;
import static java.lang.String.format;

public class UserInput {
    private final String value;
    private final InputType type;
    private final String indent;
    private final String filter;
    private final int lineNumber;
    private final int startPosition;
    private final int endPosition;

    public UserInput(String value, int spacesNumber, String filter, int lineNumber, int startPosition, int endPosition) {
        this.value = value;
        this.filter = filter;
        this.lineNumber = lineNumber;
        this.startPosition = startPosition;
        this.endPosition = endPosition;

        indent = format("%-" + spacesNumber + "s", "");
        type = StringUtils.isUnknown(value)
                ? InputType.UNKNOWN
                : isLowerCase(value.charAt(0)) ? InputType.OBJECT : InputType.TYPE;
    }

    public String getValue() {
        return value;
    }

    public String getIndent() {
        return indent;
    }

    public InputType getType() {
        return type;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return value;
    }
}
