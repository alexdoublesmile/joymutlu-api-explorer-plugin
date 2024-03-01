package com.joymutlu.apiexplorer.strategy;

import com.joymutlu.apiexplorer.model.ExploreContext;

import java.lang.reflect.Method;

public class MethodNameGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, Method method) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ctx.getUserInput())
                .append(".")
                .append(method.getName())
                .append("(")
                .append(");")
                .append("\n")
                .append(ctx.getIndent());
        return sb.toString();
    }
}
