package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;

import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static com.joymutlu.apiexplorer.util.ImportUtils.getFullClassName;
import static com.joymutlu.apiexplorer.util.ImportUtils.getPathList;
import static java.util.Optional.empty;

public final class ClassLoadService {
    private final Project project;

    public ClassLoadService(Project project) {
        this.project = project;
    }

    public PsiClass loadClass(List<String> importList, String className) throws NoClassException, NoImportException {
        final Optional<PsiClass> maybeClass = loadClassByName(getFullClassName(importList, className));
        return maybeClass.isPresent()
                ? maybeClass.get()
                : loadClassByNameList(getPathList(importList), className)
                    .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
//        final Optional<Class<?>> maybeClass = loadClassByName(getFullClassName(importList, className));
//
//        return maybeClass.isPresent()
//                ? maybeClass.get()
//                : loadClassByNameList(getPathList(importList), className)
//                .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
    }

    public Optional<PsiClass> loadClassByName(String className) throws NoClassException {
        System.out.printf("Trying find Class by name [%s]...%n", className);
        return Optional.ofNullable(JavaPsiFacade.getInstance(project)
                .findClass(className, GlobalSearchScope.allScope(project)));
//        if (className.isBlank()) {
//            return empty();
//        } else {
//            System.out.printf("Trying find Class by name [%s]...%n", className);
//            try {
//                return of(Class.forName(className));
//            } catch (ClassNotFoundException e) {
//                throw new NoClassException("Class " + className + " not found in classpath");
//            }
//        }
    }

    public Optional<PsiClass> loadClassByNameList(List<String> pathList, String className) {
        for (String path : pathList) {
            String fullClassName = path + PACKAGE_DELIMITER + className;
            System.out.printf("Trying find Class by name [%s]...%n", fullClassName);
            PsiClass psiClass = JavaPsiFacade.getInstance(project)
                    .findClass(fullClassName, GlobalSearchScope.allScope(project));
            if (psiClass != null) {
                return Optional.of(psiClass);
            }
        }
        return empty();
//        if (!pathList.isEmpty()) {
//            for (String path : pathList) {
//                try {
//                    String fullClassName = path + PACKAGE_DELIMITER + className;
//        System.out.printf("Trying find Class by name [%s]...%n", fullClassName);
//                    return of(Class.forName(fullClassName));
//                } catch (ClassNotFoundException ex) {
//                     just try next applicant
//                }
//            }
//        }
//        return empty();
    }
}
