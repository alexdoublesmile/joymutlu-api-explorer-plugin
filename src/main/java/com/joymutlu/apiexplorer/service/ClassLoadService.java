package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;

import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static com.joymutlu.apiexplorer.util.ImportUtils.getFullClassName;
import static com.joymutlu.apiexplorer.util.ImportUtils.getPathList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class ClassLoadService {
    public Class<?> loadClass(List<String> importList, String className) throws NoClassException, NoImportException {
        final Optional<Class<?>> maybeClass = loadClassByName(getFullClassName(importList, className));

        return maybeClass.isPresent()
                ? maybeClass.get()
                : loadClassByNameList(getPathList(importList), className)
                .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
    }

    public static Optional<Class<?>> loadClassByName(String className) throws NoClassException {
        if (className.isBlank()) {
            return empty();
        } else {
            System.out.printf("Trying find Class by name [%s]...%n", className);
            try {
                return of(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new NoClassException("Class " + className + " not found in classpath");
            }
        }
    }

    public static Optional<Class<?>> loadClassByNameList(List<String> pathList, String className) {
        if (!pathList.isEmpty()) {
            for (String path : pathList) {
                try {
                    String fullClassName = path + PACKAGE_DELIMITER + className;
                    System.out.printf("Trying find Class by name [%s]...%n", fullClassName);
                    return of(Class.forName(fullClassName));
                } catch (ClassNotFoundException ex) {
                    // just try next applicant
                    // TODO: 01.03.2024 find better approach
                }
            }
        }
        return empty();
    }
}
