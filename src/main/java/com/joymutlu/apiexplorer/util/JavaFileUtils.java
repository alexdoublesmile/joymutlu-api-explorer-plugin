package com.joymutlu.apiexplorer.util;

import com.joymutlu.apiexplorer.exception.NoInitializingLineException;

import java.util.Arrays;
import java.util.List;

import static com.joymutlu.apiexplorer.util.EditorConstants.NEW_LINE;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_STRING_PREFIX;
import static com.joymutlu.apiexplorer.util.StringUtils.*;
import static java.util.stream.Collectors.toList;

public final class JavaFileUtils {
    public static List<String> getImportListWithDefaults(String fileText) {
        final List<String> importList = getImportList(fileText);
        importList.add(getCurrentPackage(fileText));
        importList.addAll(ImportUtils.DEFAULT_PACKAGES);
        return importList;
    }

    public static List<String> getImportList(String fileText) {
        return Arrays.stream(fileText.split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .collect(toList());
    }

    public static String getCurrentPackage(String fileText) {
        return fileText.substring(
                PACKAGE_STRING_PREFIX.length(),
                fileText.indexOf(NEW_LINE) - 1);
    }

    public static String findClassNameByObject(String fileText, String objName) throws NoInitializingLineException {
        System.out.printf("Defining object [%s] type...%n", objName);
        String initLine = StringUtils.findInitializingLine(fileText, objName)
                .orElseThrow(NoInitializingLineException::new);
        System.out.printf("Initialization line: [%s]%n", initLine);

        final String[] initLineElements = initLine.trim().split("[ ;=(),]");
        System.out.printf("Init line Elements: [%s]%n", Arrays.toString(initLineElements));

        String objectType = resolveType(initLineElements, objName);
        System.out.printf("Object type defined as [%s]%n", objectType);
        return objectType;
    }

    private static String resolveType(String[] elements, String referenceName) throws NoInitializingLineException {
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i].trim();
            if (element.equals(referenceName)) {
                String typeDeclaration = elements[i - 1].trim();
                if (isInvalidDeclaration(typeDeclaration)) {
                    typeDeclaration = resolveLowerCaseType(elements, i, typeDeclaration);
                }
                if (isGenericDeclaration(typeDeclaration)) {
                    typeDeclaration = StringUtils.stripGenerics(resolveTypeFromGeneric(elements, i - 1));
                }
                return isArray(typeDeclaration) ? "Array" : StringUtils.stripGenerics(typeDeclaration);
            }
            if (element.contains(referenceName) && isDirtyVarargOrArray(element, referenceName)) {
                return "Array";
            }
        }
        throw new NoInitializingLineException();
    }

    private static boolean isInvalidDeclaration(String typeDeclaration) {
        return typeDeclaration.isEmpty() || isLowerCase(typeDeclaration);
    }

    private static String resolveTypeFromGeneric(String[] elements, int idx) throws NoInitializingLineException {
        int depth = 0;
        for (int i = idx; i >= 0; i--) {
            final String element = elements[i];
            if (element.contains(">")) {
                for (int j = element.length() - 1; j >= 0; j--) {
                    char ch = element.charAt(j);
                    if (ch == '>') {
                        depth++;
                    }
                }
            }
            if (element.contains("<")) {
                for (int j = element.length() - 1; j >= 0; j--) {
                    char ch = element.charAt(j);
                    if (ch == '<') {
                        depth--;
                    }
                }
            }
            if (depth == 0) {
                return element;
            }
        }
        throw new NoInitializingLineException();
    }

    private static String resolveLowerCaseType(String[] elements, int idx, String typeDeclaration) throws NoInitializingLineException {
        if (!typeDeclaration.isEmpty() && isArray(typeDeclaration)) {
            return "Array";
        }
        if (typeDeclaration.isEmpty() || isUndefinedLowerType(typeDeclaration)) {
            for (int i = idx; i >= 0; i--) {
                final String element = elements[i];
                System.out.printf("Scanning for lowercase type [%s]", element);
                if (!element.isEmpty() && isUpperCase(element)) {
                    return element;
                }
            }
        }
        throw new NoInitializingLineException();
    }
}
