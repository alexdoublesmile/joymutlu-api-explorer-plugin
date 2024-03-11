package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.UserInput;

public interface ClassFindStrategy {
    PsiClass findClass(UserInput userInput, String fileText, Project project) throws NoImportException, PrimitiveTypeException, NoInitializingLineException, UnknownInputException;
}
