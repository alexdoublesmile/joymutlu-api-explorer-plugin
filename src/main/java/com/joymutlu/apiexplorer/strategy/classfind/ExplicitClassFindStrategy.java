package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.service.ClassFindService;
import com.joymutlu.apiexplorer.util.JavaFileUtils;

public class ExplicitClassFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project) throws NoImportException, PrimitiveTypeException {
        String className = userInput.getValue();

        final PsiClass exploredClass = ClassFindService.findClass(className, JavaFileUtils.getFullImportList(fileText), project);

        return exploredClass;
    }
}
