package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.UserInput;

public class UnknownStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project) throws UnknownInputException {
        throw new UnknownInputException();
    }

    @Override
    public String getName() {
        return "";
    }
}
