package com.joymutlu.apiexplorer.strategy.codegeneration;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.strategy.codegeneration.CodeGenerationStrategy;

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
