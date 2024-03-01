package com.joymutlu.apiexplorer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.joymutlu.apiexplorer.util.EditorConstants.*;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public final class ImportUtils {
    public static List<String> getImportList(CharSequence editorCode) {
        final ArrayList<String> importList = new ArrayList<>();
        System.out.println("Scanning imports...");
        Scanner scanner = new Scanner(editorCode.toString()).useDelimiter("\n");
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (line.startsWith(IMPORT_STRING_PREFIX)) {
                importList.add(line);
            }
        }
        return importList;
    }

    public static List<String> getAsteriskDeclarations(List<String> importList) {
        System.out.println("Collecting asterisk and default declarations...");
        List<String> classPathApplicants = importList.stream()
                .filter(s -> s.endsWith(ASTERISK_DECLARATION))
                .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - ASTERISK_DECLARATION.length()))
                .collect(toList());

        addDefaultPackages(classPathApplicants);
        System.out.println(classPathApplicants.isEmpty()
                ? "No asterisk declarations in imports"
                : format("Path applicants: [%s]", classPathApplicants));

        return classPathApplicants;
    }

    public static String getFullClassName(List<String> importList, String className) {
        String fullClassName = importList.stream()
                .filter(importStr -> {
                    final boolean hasClassName = importStr.endsWith(className + DECLARATION_DELIMITER);
                    System.out.println(hasClassName
                            ? format("[%s] has class name", importStr)
                            : format("[%s] doesn't fit", importStr));
                    return hasClassName;
                })
                .filter(importStr -> {
                    final boolean isFit = PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2);
                    System.out.println(isFit
                            ? format("[%s] exactly fits!", importStr)
                            : format("[%s] doesn't exactly fit", importStr));
                    return isFit;
                })
                .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - 1))
                .findFirst()
                .orElse("");
        System.out.println(fullClassName.isBlank()
                ? "Necessary Class was not found in imports"
                : format("Full Class name: [%s]", fullClassName));
        return fullClassName;
    }

    private static void addDefaultPackages(List<String> classPathApplicants) {
        classPathApplicants.add("java.lang");
    }
}
