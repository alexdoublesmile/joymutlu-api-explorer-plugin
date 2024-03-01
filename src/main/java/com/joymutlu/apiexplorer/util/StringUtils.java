package com.joymutlu.apiexplorer.util;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static java.util.Optional.empty;

public final class StringUtils {
    public static String getArgDefaultValuesString(List<String> argTypes) {
        String result = "";
        for (int i = 0; i < argTypes.size(); i++) {
            final String paramType = argTypes.get(i);
            result += getArgDefaultValue(paramType);
            if (i < argTypes.size() - 1) {
                result += ", ";
            }
        }
        return result;
    }

    private static String getArgDefaultValue(String paramType) {
        switch (paramType) {
            case "Byte" :
            case "Short" :
            case "Integer" : return "1";
            case "Long" : return "1L";
            case "Float" : return "1.0f";
            case "Double" : return "1.0";
            case "Boolean" : return "true";
            case "Character" : return "'c'";
            case "String" : return "str";
            default: return "new Object()";
        }
    }

    public static int calcSpaces(String line, int offset) {
        int indentCount = 0;
        for (int i = 0; i < offset && i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                indentCount++;
            }
        }
        System.out.printf("Defined %s spaces%n", indentCount);
        return indentCount;
    }

    public static Optional<String> findInitializingLine(CharSequence editorCode, String objectName) {
        Scanner scanner = new Scanner(editorCode.toString());
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(objectName + ";") || line.contains(objectName + " =")) {
                return Optional.of(line);
            }
        }
        return empty();
    }
}
