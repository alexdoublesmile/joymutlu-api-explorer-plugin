package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.ExploreContext;

import java.lang.reflect.Method;

public class MethodNameGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, PsiMethod method) {
        return new StringBuilder()
                .append(ctx.getUserInput())
                .append(".")
                .append(method.getName())
                .append("(")
                .append(");")
                .append("\n")
                .append(ctx.getIndent())
                .toString();
    }
}
