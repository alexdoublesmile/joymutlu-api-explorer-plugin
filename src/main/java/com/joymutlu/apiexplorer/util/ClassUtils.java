package com.joymutlu.apiexplorer.util;

import java.util.Set;

public final class ClassUtils {
    public static final Set<String> PRIMITIVE_SET = Set.of(
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "char",
            "boolean"
    );

    public static boolean isNotTopClass(Class<?> parent) {
        return parent != null && !parent.getName().equals("java.lang.Object");
    }
}
