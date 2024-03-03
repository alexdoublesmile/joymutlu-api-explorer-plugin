package com.joymutlu.apiexplorer.strategy;

import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;

public class FullDeclarationGenerationStrategy implements CodeGenerationStrategy {
    @Override
    public String generateApiLine(ExploreContext ctx, Method method) {
        return new StringBuilder()
                .append(resolveReference(method))
                .append(ctx.getUserInput())
                .append(".")
                .append(method.getName())
                .append("(")
                .append(StringUtils.getArgsString(
                        stream(method.getParameterTypes())
                                .map(Class::getName)
                                .toList()))
                .append(");")
                .append("\n")
                .append(ctx.getIndent())
                .toString();
    }

    private String resolveReference(Method method) {
        final Class<?> returnType = method.getReturnType();
        if (returnType.getName().equals("void")) {
            return "";
        }
        return new StringBuilder()
                .append(resolveReturnType(returnType))
                .append(" ")
                .append(method.getName() + resolveSuffix(method))
                .append(" = ")
                .toString();
    }

    private String resolveReturnType(Class<?> returnType) {
        return returnType.getName().equals("[Ljava.lang.Object;")
                ? "Object[]"
                : returnType.getSimpleName();
    }

    private String resolveSuffix(Method method) {
        if (method.getParameterTypes().length == 0) {
            return "";
        }
        final String result = "By" + stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .map(suffix -> suffix.replace("[]", "Array"))
                .collect(StringBuilder::new, (sb1, sb2) -> sb1.append(sb2).append("And"), StringBuilder::append);
        return result.substring(0, result.length() - 3);
    }
}
