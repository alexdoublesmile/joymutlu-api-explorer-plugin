package com.joymutlu.apiexplorer.util;

import com.joymutlu.apiexplorer.exception.NoClassException;

import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class ClassUtils {
    public static Optional<Class<?>> findClassByName(String className) throws NoClassException {
        if (className.isBlank()) {
            return empty();
        } else {
            System.out.printf("Trying find Class by name: [%s]...%n", className);
            try {
                return of(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new NoClassException("Class " + className + " not found in classpath");
            }
        }
    }

    public static Optional<Class<?>> findClassByNameList(List<String> pathList, String className) {
        if (!pathList.isEmpty()) {
            for (String path : pathList) {
                try {
                    String fullClassName = path + PACKAGE_DELIMITER + className;
                    System.out.printf("Trying find Class by name: [%s]...%n", fullClassName);
                    return of(Class.forName(fullClassName));
                } catch (ClassNotFoundException ex) {
                    System.out.println("No such file...");
                    // just try next applicant
                    // TODO: 01.03.2024 find better approach
                }
            }
        }
        return empty();
    }
}
