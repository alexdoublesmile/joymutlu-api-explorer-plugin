package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.UserInput;

public class MethodNameGenerationStrategy implements CodeGenerationStrategy {

    @Override
    public String generateApiLine(UserInput userInput, PsiMethod method) {
        return new StringBuilder()
                .append(userInput)
                .append(".")
                .append(method.getName())
                .append("(")
                .append(");")
                .append("\n")
                .append(userInput.getIndent())
                .toString();
    }
}
