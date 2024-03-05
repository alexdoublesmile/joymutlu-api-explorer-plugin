package com.joymutlu.apiexplorer.util;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

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

    public static final Set<String> OBJECT_METHODS = new HashSet<>(asList(
            "equals",
            "hashCode",
            "getClass",
            "finalize",
            "wait",
            "notify",
            "notifyAll",
            "toString",
            "clone",
            "Object"
    ));
}
