package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.ExploreContext;

import java.lang.reflect.Method;

public interface CodeGenerationStrategy {
    String generateApiLine(ExploreContext ctx, PsiMethod method);
}
