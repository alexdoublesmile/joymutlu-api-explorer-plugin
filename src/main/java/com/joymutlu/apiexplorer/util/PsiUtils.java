package com.joymutlu.apiexplorer.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;

import java.util.ArrayList;
import java.util.List;

import static com.joymutlu.apiexplorer.util.ClassConstants.VOID;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_DELIMITER;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class PsiUtils {

    public static boolean isStatic(PsiMethod method) {
        return method.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    }

    public static boolean isVirtual(PsiMethod method) {
        return !isStatic(method);
    }

    public static boolean isAbstract(PsiMethod method) {
        return method.getModifierList().hasExplicitModifier(PsiModifier.ABSTRACT);
    }

    public static boolean isNotAbstract(PsiMethod method) {
        return !isAbstract(method);
    }

    public static boolean isNotDeprecated(PsiMethod method) {
        return !isDeprecated(method);
    }

    public static boolean isDeprecated(PsiMethod method) {
        return method.isDeprecated();
    }

    public static boolean isNotObjectMethod(PsiMethod method) {
        return !ClassConstants.OBJECT_METHODS.contains(method.getName());
    }

    public static List<PsiMethod> getStaticApi(PsiClass clazz) {
        System.out.printf("Collecting all public static methods from %s...%n", clazz.getName());
        return stream(clazz.getMethods())
                .filter(PsiUtils::isPublic)
                .filter(PsiUtils::isStatic)
                .filter(PsiUtils::isNotObjectMethod)
                .collect(toList());
    }

    private static boolean isPublic(PsiMethod method) {
        return method.getModifierList().hasExplicitModifier(PsiModifier.PUBLIC) && !method.isConstructor();
    }

    public static List<PsiMethod> getVirtualApi(PsiClass clazz, boolean withParentApi) {
        System.out.printf("Collecting all public methods from %s...%n", clazz.getName());
        final List<PsiMethod> result = stream(clazz.getMethods())
                .filter(PsiUtils::isPublic)
                .filter(PsiUtils::isVirtual)
                .filter(PsiUtils::isNotObjectMethod)
                .collect(toList());

        if (withParentApi) {
            final PsiClass parent = clazz.getSuperClass();
            final List<PsiClass> interfaces = asList(clazz.getInterfaces());
            if (isNotTopClass(parent)) {
                result.addAll(getVirtualApi(parent, true));
            }
            interfaces.forEach(i -> result.addAll(getVirtualApi(i, true)));
        }
        return result;
    }

    public static boolean isNotTopClass(PsiClass parent) {
        return parent != null && !parent.getName().equals("java.lang.Object");
    }

    public static List<PsiMethod> removeDeprecated(List<PsiMethod> methods) {
        System.out.println("Removing deprecated methods...");
        return methods.stream()
                .filter(PsiUtils::isNotDeprecated)
                .collect(toList());
    }

    public static List<PsiMethod> removeOverloads(List<PsiMethod> methods) {
        System.out.println("Removing overloaded methods...");
        return new ArrayList<>(methods.stream()
                .collect(toMap(PsiMethod::getName, identity(), (m1, m2) -> m1))
                .values());
    }

    public static PsiClass findClass(String className, List<String> importList, Project project) throws NoImportException, PrimitiveTypeException {
        if (ClassConstants.PRIMITIVE_SET.contains(className) || VOID.equals(className)) {
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
