package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.util.StringUtils;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.String.format;

public class UserInput {
    private final String value;
    private final InputType type;
    private final ApiType apiType;
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
        type = initInputType(value);
        apiType = initApiType(type);
    }

    private ApiType initApiType(InputType inputType) {
        switch (inputType) {
            case TYPE: return ApiType.STATIC;
            case OBJECT:
            case STATIC_METHOD:
            case VIRTUAL_METHOD: return ApiType.VIRTUAL;
            default: return ApiType.UNKNOWN;
        }
    }

    private InputType initInputType(String value) {
        if (StringUtils.isUnknown(value)) {
            return InputType.UNKNOWN;
        }
        final String[] splittedInput = value.split("\\.");
        if (splittedInput.length == 1) {
            if (isLowerCase(value.charAt(0))) {
                return InputType.OBJECT;
            } else {
                return InputType.TYPE;
            }
        }
        if (isUpperCase(splittedInput[splittedInput.length - 2].charAt(0))) {
            return InputType.STATIC_METHOD;
        } else {
            return InputType.VIRTUAL_METHOD;
        }
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

    public ApiType getApiType() {
        return apiType;
    }

    @Override
    public String toString() {
        return value;
    }
}
