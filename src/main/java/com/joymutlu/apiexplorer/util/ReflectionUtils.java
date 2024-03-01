package com.joymutlu.apiexplorer.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtils {
    public static final Set<String> OBJECT_METHODS = new HashSet<>(Arrays.asList(
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
}
