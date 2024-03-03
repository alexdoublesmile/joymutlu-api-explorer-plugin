package com.joymutlu.apiexplorer.util;

import com.joymutlu.apiexplorer.model.MethodDeclaration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.joymutlu.apiexplorer.util.ClassUtils.isNotTopClass;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class ReflectionUtils {
    public static final Set<String> OBJECT_METHODS = new HashSet<>(asList(
            "equals",
            "hashCode",
            "getClass",
            "finalize",
            "wait",
            "notify",
            "notifyAll",
            "toString",
            "clone"
    ));

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isVirtual(Method method) {
        return !isStatic(method);
    }

    public static boolean isAbstract(Method method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    public static boolean isNotAbstract(Method method) {
        return !isAbstract(method);
    }

    public static boolean isNotDeprecated(Method method) {
        return !isDeprecated(method);
    }

    public static boolean isDeprecated(Method method) {
        return method.getAnnotation(Deprecated.class) != null;
    }

    public static boolean isNotObjectMethod(Method method) {
        return !OBJECT_METHODS.contains(method.getName());
    }

    public static Map<MethodDeclaration, Method> getStaticApi(Class<?> clazz) {
        System.out.printf("Collecting all public static methods from %s...%n", clazz);
        return stream(clazz.getMethods())
                .filter(ReflectionUtils::isStatic)
                .filter(ReflectionUtils::isNotObjectMethod)
                .collect(toMap(method ->
                                new MethodDeclaration(method.getName(), asList(method.getParameterTypes())),
                        identity()));
    }

    public static Map<MethodDeclaration, Method> getVirtualApi(Class<?> clazz, boolean withParentApi) {
        System.out.printf("Collecting all public methods from %s...%n", clazz);
        final Map<MethodDeclaration, Method> result = stream(clazz.getMethods())
                .filter(ReflectionUtils::isVirtual)
                .filter(ReflectionUtils::isNotObjectMethod)
                .collect(toMap(method ->
                        new MethodDeclaration(method.getName(), asList(method.getParameterTypes())),
                        identity()));

        if (withParentApi) {
            final Class<?> parent = clazz.getSuperclass();
            final List<Class<?>> interfaces = asList(clazz.getInterfaces());
            if (isNotTopClass(parent)) {
                result.putAll(getVirtualApi(parent, true));
            }
            interfaces.forEach(i -> result.putAll(getVirtualApi(i, true)));
        }
        return result;
    }

    public static Map<MethodDeclaration, Method> removeDeprecated(Map<MethodDeclaration, Method> methods) {
        System.out.println("Removing deprecated methods...");
        return methods.entrySet().stream()
                .filter(entry -> ReflectionUtils.isNotDeprecated(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<MethodDeclaration, Method> removeOverloads(Map<MethodDeclaration, Method> methods) {
        System.out.println("Removing overloaded methods...");
        return methods.values().stream()
                .collect(toMap(Method::getName, identity(), (m1, m2) -> m1))
                .values().stream()
                .collect(Collectors.toMap(method ->
                        new MethodDeclaration(method.getName(), asList(method.getParameterTypes())),
                        identity()));
    }
}
