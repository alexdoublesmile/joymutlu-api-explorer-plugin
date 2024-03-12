package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.*;
import com.joymutlu.apiexplorer.model.UserInput;

public interface ClassFindStrategy {
    PsiClass findClass(UserInput userInput, String fileText, Project project)
            throws NoImportException, PrimitiveTypeException, NoInitializingLineException, UnknownInputException, NoMethodException;

    String getName();
}
