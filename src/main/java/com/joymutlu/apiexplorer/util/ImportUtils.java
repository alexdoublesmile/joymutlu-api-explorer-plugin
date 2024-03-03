package com.joymutlu.apiexplorer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public final class ImportUtils {
    public static final List<String> DEFAULT_PACKAGES = List.of(
            "java.lang",
            "java.lang.reflect"
    );

    public static List<String> getImportList(String editorCode) {
        System.out.println("Scanning imports...");
        return Arrays.stream(editorCode.split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .toList();
    }

    public static List<String> getPathList(List<String> importList) {
        System.out.println("Collecting asterisk and default declarations...");

        final ArrayList<String> result = new ArrayList<>();
        result.addAll(DEFAULT_PACKAGES);
        result.addAll(getAsteriskDeclarations(importList));
        System.out.printf("Path applicants: %s%n", result);
        return result;
    }

    public static List<String> getAsteriskDeclarations(List<String> importList) {
        return importList.stream()
                .filter(s -> s.endsWith(EditorConstants.ASTERISK_DECLARATION))
                .map(s -> s.substring(
                        EditorConstants.IMPORT_STRING_PREFIX.length(),
                        s.length() - EditorConstants.ASTERISK_DECLARATION.length()))
                .toList();
    }

    public static String getFullClassName(List<String> importList, String className) {
        String fullClassName = importList.stream()
                .filter(importStr -> {
                    final boolean hasClassName = importStr.endsWith(className + EditorConstants.DECLARATION_DELIMITER);
                    System.out.println(hasClassName
                            ? format("[%s] has class name", importStr)
                            : format("[%s] doesn't fit", importStr));
                    return hasClassName;
                })
                .filter(importStr -> {
                    final boolean isFit = EditorConstants.PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2);
                    System.out.println(isFit
                            ? format("[%s] exactly fits!", importStr)
                            : format("[%s] doesn't exactly fit", importStr));
                    return isFit;
                })
                .map(s -> s.substring(EditorConstants.IMPORT_STRING_PREFIX.length(), s.length() - 1))
                .findFirst()
                .orElse("");
        System.out.println(fullClassName.isBlank()
                ? "Necessary Class was not found in imports"
                : format("Full Class name: [%s]", fullClassName));
        return fullClassName;
    }
}
