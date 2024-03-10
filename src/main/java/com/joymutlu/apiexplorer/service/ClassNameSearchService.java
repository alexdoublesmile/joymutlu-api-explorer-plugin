package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.Arrays;

import static com.joymutlu.apiexplorer.util.StringUtils.*;

public final class ClassNameSearchService {
    private final String editorText;

    public ClassNameSearchService(String editorText) {
        this.editorText = editorText;
    }

    public String findClassNameByObject(String input) throws NoInitializingLineException {
        System.out.printf("Defining object [%s] type...%n", input);
        String initLine = StringUtils.findInitializingLine(editorText, input)
                .orElseThrow(NoInitializingLineException::new);
        System.out.printf("Initialization line: [%s]%n", initLine);

        final String[] initLineElements = initLine.trim().split("[ ;=(),]");
        System.out.printf("Elements: [%s]%n", Arrays.toString(initLineElements));

        String objectType = resolveType(initLineElements, input);
        System.out.printf("Object type defined as [%s]%n", objectType);
        return objectType;
    }

    private String resolveType(String[] elements, String referenceName) throws NoInitializingLineException {
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

    private boolean isInvalidDeclaration(String typeDeclaration) {
        return typeDeclaration.isEmpty() || isLowerCase(typeDeclaration);
    }

    private String resolveTypeFromGeneric(String[] elements, int idx) throws NoInitializingLineException {
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

    private String resolveLowerCaseType(String[] elements, int idx, String typeDeclaration) throws NoInitializingLineException {
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
