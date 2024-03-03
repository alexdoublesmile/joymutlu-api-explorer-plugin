package com.joymutlu.apiexplorer.strategy;

import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;

public class FullDeclarationGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, Method method) {
        final StringBuilder sb = new StringBuilder();
        final String returnType = method.getReturnType().getName();
        sb.append(returnType)
                .append(" ")
                .append(returnType.toLowerCase())
                .append(" = ")
                .append(ctx.getUserInput())
                .append(".")
                .append(method.getName())
                .append("(")
                .append(StringUtils.getArgDefaultValuesString(
                        stream(method.getParameterTypes())
                                .map(Class::getName)
                                .toList()))
                .append(");")
                .append("\n")
                .append(ctx.getIndent());
        return sb.toString();
    }
}
