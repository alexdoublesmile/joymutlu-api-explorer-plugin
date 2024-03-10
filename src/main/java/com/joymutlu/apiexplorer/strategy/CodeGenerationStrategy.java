package com.joymutlu.apiexplorer.strategy;

import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.model.UserInput;

public interface CodeGenerationStrategy {
    String generateApiLine(UserInput userInput, PsiMethod method);
}
