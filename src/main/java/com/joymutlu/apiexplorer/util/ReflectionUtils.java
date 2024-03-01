package com.joymutlu.apiexplorer.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {
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
}
