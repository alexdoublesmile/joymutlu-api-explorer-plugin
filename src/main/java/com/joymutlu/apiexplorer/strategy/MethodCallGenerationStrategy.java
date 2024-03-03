package com.joymutlu.apiexplorer.strategy;

import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;

public class MethodCallGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, Method method) {
        return new StringBuilder()
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
                .append(ctx.getIndent())
                .toString();
    }
}
