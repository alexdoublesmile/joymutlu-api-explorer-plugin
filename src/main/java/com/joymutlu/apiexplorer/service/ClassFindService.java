package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;

import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static com.joymutlu.apiexplorer.util.ImportUtils.getDirectClassName;
import static com.joymutlu.apiexplorer.util.ImportUtils.getImplicitPackages;
import static java.util.Optional.empty;

public final class ClassFindService {
    private final UserInputService userInputService;

    public ClassFindService(UserInputService userInputService) {
        this.userInputService = userInputService;
    }

    public PsiClass findClass(Project project) throws UnknownInputException, NoInitializingLineException, NoImportException {
        final List<String> importList = userInputService.getImportList();
        final String className = userInputService.findClassName();
        final Optional<PsiClass> maybeClass = findClassByName(getDirectClassName(importList, className), project);
        return maybeClass.isPresent()
                ? maybeClass.get()
                : findClassByNameList(getImplicitPackages(importList), className, project)
                .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
    }

    private Optional<PsiClass> findClassByName(String className, Project project) {
        System.out.printf("Trying find Class by name [%s]...%n", className);
        return Optional.ofNullable(JavaPsiFacade.getInstance(project)
                .findClass(className, GlobalSearchScope.allScope(project)));
    }

    private Optional<PsiClass> findClassByNameList(List<String> pathList, String className, Project project) {
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
    }
}
