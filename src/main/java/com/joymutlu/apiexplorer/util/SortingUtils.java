package com.joymutlu.apiexplorer.util;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.SortingType;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public final class SortingUtils {
    public static Comparator<? super PsiMethod> getSorting(SortingType sortingType) {

        if (sortingType == SortingType.NAME_ONLY) {
            return comparing(PsiMethod::getName);
        } else {
            return comparing((PsiMethod method) -> {
                String name = method.getName();
                return name.startsWith("is") ? 1
                        : name.startsWith("has") ? 2
                        : name.startsWith("get") ? 3
                        : name.startsWith("set") ? 4
                        : name.startsWith("to") ? 5
                        : !name.startsWith("compare")
                        && !name.startsWith("close") ? 6
                        : !name.startsWith("close") ? 7
                        : 8;
            }).thenComparing(PsiMethod::getName);
        }
    }
}
