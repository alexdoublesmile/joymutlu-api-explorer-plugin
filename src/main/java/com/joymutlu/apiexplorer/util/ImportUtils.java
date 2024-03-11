package com.joymutlu.apiexplorer.util;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public final class ImportUtils {
    public static final List<String> DEFAULT_PACKAGES = new ArrayList<>(asList(
            "java.lang",
            "java.lang.reflect"
    ));

    public static List<String> getImplicitPackages(List<String> importList, String selfPackage) {
        System.out.println("Collecting asterisk and default declarations...");

        final ArrayList<String> result = new ArrayList<>();
        result.add(selfPackage);
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
                .collect(toList());
    }

    public static String getDirectClassName(List<String> importList, String className) {
        String fullClassName = importList.stream()
                .filter(importStr -> importStr.endsWith(className + EditorConstants.DECLARATION_DELIMITER))
                .filter(importStr -> EditorConstants.PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2))
                .map(s -> s.substring(EditorConstants.IMPORT_STRING_PREFIX.length(), s.length() - 1))
                .findFirst()
                .orElse("");
        System.out.println(fullClassName.isEmpty()
                ? format("Necessary Class [%s] was not found in imports", className)
                : format("Full Class name found in imports: [%s]", fullClassName));
        return fullClassName;
    }
}
