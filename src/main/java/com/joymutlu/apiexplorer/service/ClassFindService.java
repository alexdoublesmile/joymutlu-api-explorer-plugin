package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.util.ClassUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.List;

import static com.joymutlu.apiexplorer.util.ClassUtils.VOID;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;

public final class ClassFindService {
    public static PsiClass findClass(String className, List<String> importList, Project project) throws NoImportException, PrimitiveTypeException {
        if (ClassUtils.PRIMITIVE_SET.contains(className) || VOID.equals(className)) {
            throw new PrimitiveTypeException("You can't load methods from '" + className + "' type");
        }
        for (String importStr : importList) {
            final String path = StringUtils.resolveImport(importStr, className);
            String fullClassName = path + PACKAGE_DELIMITER + className;

            System.out.printf("Trying find Class by name [%s]...%n", fullClassName);
            PsiClass psiClass = JavaPsiFacade.getInstance(project)
                    .findClass(fullClassName, GlobalSearchScope.allScope(project));
            if (psiClass != null) {
                return psiClass;
            }
        }
        throw new NoImportException("Import for '" + className + "' class not found. Declare import first");
    }
}
