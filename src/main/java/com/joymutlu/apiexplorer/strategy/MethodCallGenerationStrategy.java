package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.StringUtils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class MethodCallGenerationStrategy implements CodeGenerationStrategy {

    @Override
    public String generateApiLine(UserInput userInput, PsiMethod method) {
        return new StringBuilder()
                .append(userInput)
                .append(".")
                .append(method.getName())
                .append("(")
                .append(StringUtils.getArgsString(
                        stream(method.getParameterList().getParameters())
                                .map(param -> param.getType().toString())
                                .collect(toList())))
                .append(");")
                .append("\n")
                .append(userInput.getIndent())
                .toString();
    }
}
