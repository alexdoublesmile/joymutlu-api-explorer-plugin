package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;

public class MethodCallGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, PsiMethod method) {
        return new StringBuilder()
                .append(ctx.getUserInput())
                .append(".")
                .append(method.getName())
                .append("(")
                .append(StringUtils.getArgsString(
                        stream(method.getParameterList().getParameters())
                        .map(param -> param.getType().toString())
                        .toList()))
                .append(");")
                .append("\n")
                .append(ctx.getIndent())
                .toString();
    }
}
