package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.ClassUtils;
import com.joymutlu.apiexplorer.util.ImportUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.StringUtils.*;

public final class ClassSearchService {
    public Class<?> findClass(List<String> importList, String className) throws NoClassException, NoImportException {
        final Optional<Class<?>> maybeClass = ClassUtils.findClassByName(
                ImportUtils.getFullClassName(importList, className));

        return maybeClass.isPresent()
                ? maybeClass.get()
                : ClassUtils.findClassByNameList(ImportUtils.getAsteriskDeclarations(importList), className)
                .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
    }

    public String findClassName(ExploreContext ctx, CharSequence code) throws UnknownInputException, NoInitializingLineException {
        switch (ctx.getInputType()) {
            case TYPE: return ctx.getUserInput().value();
            case OBJECT: return defineClassFromObject(ctx, code);
            default: throw new UnknownInputException();
        }
    }

    private String defineClassFromObject(ExploreContext ctx, CharSequence editorCode) throws NoInitializingLineException {
        final String input = ctx.getUserInput().value();
        System.out.printf("Defining object [%s] type...%n", input);
        String line = StringUtils.findInitializingLine(editorCode, input)
                .orElseThrow(NoInitializingLineException::new);
        System.out.printf("Initialization line: [%s]%n", line);

        final String[] lineElements = line.trim().split("[ ;=(),]");
        System.out.printf("Elements: [%s]%n", Arrays.toString(lineElements));

        String objectType = resolveType(lineElements, input);
        System.out.printf("Object type defined as [%s]%n", objectType);
        return objectType;
    }

    private String resolveType(String[] elements, String referenceName) throws NoInitializingLineException {
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i].trim();
            if (element.equals(referenceName)) {
                String typeDeclaration = elements[i - 1].trim();
                if (typeDeclaration.isBlank() || isLowerCase(typeDeclaration)) {
                    typeDeclaration = resolveLowerCaseType(elements, i, typeDeclaration);
                }
                if (isGenericDeclaration(typeDeclaration)) {
                    typeDeclaration = filterGeneric(resolveTypeFromGeneric(elements, i - 1));
                }
                return isArray(typeDeclaration) ? "Array" : filterGeneric(typeDeclaration);
            }
            if (element.contains(referenceName) && isDirtyVarargOrArray(element, referenceName)) {
                return "Array";
            }
        }
        throw new NoInitializingLineException();
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
        if (!typeDeclaration.isBlank() && isArray(typeDeclaration)) {
            return "Array";
        }
        if (typeDeclaration.isBlank() || isUndefined(typeDeclaration)) {
            for (int i = idx; i >= 0; i--) {
                final String element = elements[i];
                System.out.printf("Scanning for lowercase type [%s]", element);
                if (!element.isBlank() && isUpperCase(element)) {
                    return element;
                }
            }
        }
        throw new NoInitializingLineException();
    }
}
